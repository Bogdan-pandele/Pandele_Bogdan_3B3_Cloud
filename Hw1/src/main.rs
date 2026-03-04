use std::{fs::{self}, io::{Read, Write}, net::{TcpListener, TcpStream}, path::Path, str::from_utf8, sync::{Arc, RwLock}, thread};



use anyhow::anyhow;
use serde::Deserialize;
use uuid::Uuid;

use crate::models::{Gender, SchoolDatabase, Student, StudentClass, seed_database};
mod models;


fn build_response(status: &str, body: &str) -> String {
    format!("HTTP/1.1 {}\r\nContent-Type: application/json\r\nContent-Length: {}\r\nConnection: close\r\n\r\n{}", status, body.len(), body)
}

fn handle_request(method: &str, path: &str, body: &[u8], db: Arc<RwLock<SchoolDatabase>>) -> anyhow::Result<String>{
    let path_parts: Vec<&str> = path.split("/").filter(|&s| !s.is_empty()).collect();
    match (method, path_parts.as_slice()) {
        ("GET", ["students"]) => {
            let db_guard = db.read().map_err(|e|anyhow::anyhow!("Lock poisoned: {e}"))?;
            let json = serde_json::to_string(&db_guard.students)?;
            return Ok(build_response("200 OK", &json));
        }

        ("GET", ["students", id]) => {
            let db_guard = db.read().map_err(|e|anyhow::anyhow!("Lock poisoned: {e}"))?;            
            if let Some(s) = db_guard.students.iter().find(|&s| s.id.to_string() == *id) {
                let json = serde_json::to_string(s)?;
                return Ok(build_response("200 OK", &json));
            } else {
                return Ok(build_response("404 Not Found", "{\"error\": \"Student doesn't exist\"}"));
            }
        }

        ("GET", ["teachers"]) => {
            let db_guard = db.read().map_err(|e|anyhow::anyhow!("Lock poisoned: {e}"))?;
            let json  = serde_json::to_string(&db_guard.teachers)?;
            return Ok(build_response("200 OK", &json)); 
        }

        ("GET", ["teachers", id]) => {
            let db_guard = db.read().map_err(|e|anyhow::anyhow!("Lock poisoned: {e}"))?;
            if let Some(t) = db_guard.teachers.iter().find(|&t| t.id.to_string() == *id) {
                let json = serde_json::to_string(t)?;
                return Ok(build_response("200 OK", &json));
            } else {
                return Ok(build_response("404 Not Found", "{\"error\": \"Teacher doesn't exist\"}"));
            }
        }
        


        ("POST", ["students"]) => {
            let body_str = from_utf8(body).unwrap_or("").trim_matches(char::from(0));
            let new_student: Student = serde_json::from_str(body_str)?;
            {
                let mut db_guard = db.write().map_err(|e|anyhow::anyhow!("Lock poisoned: {e}"))?;

                if db_guard.students.iter().any(|s|s.id == new_student.id) {
                    return Ok(build_response("409 Conflict", "{\"error\": \"Id already exists\"}"));
                }

                db_guard.students.push(new_student);
                let json = serde_json::to_string_pretty(&*db_guard)?;
                fs::write("database.json", json)?;
            }

            Ok(build_response("201 Created", "{\"status\": \"Success\"}"))
        }
        
        ("POST", ["students", student_id_str, "grades"]) => {
            let student_id = uuid::Uuid::parse_str(student_id_str)?;

            #[derive(Deserialize)]
            struct GradeRequest {
                class_id: Uuid,
                score: u8
            }
            let body_str = from_utf8(body).unwrap_or("").trim_matches(char::from(0));            
            let payload: GradeRequest = serde_json::from_str(body_str)?;
            {
                let mut db_guard = db.write().map_err(|e|anyhow::anyhow!("Lock poisoned: {e}"))?;
                if !db_guard.students.iter().any(|s| s.id == student_id) {
                    return Ok(build_response("404 Not found", "{\"error\": \"Student doesn't exist\"}"));
                }
                
                if db_guard.student_classes.iter().any(|c| c.class_id == payload.class_id && c.student_id == student_id) {
                    return Ok(build_response("409 Conflict", "{\"error\": \"Student is already graded for this class\"}"));
                }

                if payload.score > 10 {
                    return Ok(build_response("400 Bad Request", "{\"error\": \"Invalid score value\"}"));
                }
                
                db_guard.student_classes.push(StudentClass { student_id, class_id: payload.class_id, score: payload.score });
                let json = serde_json::to_string_pretty(&*db_guard)?;
                fs::write("database.json", json)?;
            }   
            Ok(build_response("201 Created", "{\"status\": \"Success\"}"))
        }
        
        ("PUT", ["students", student_id_str]) => {
            let student_id = Uuid::parse_str(student_id_str)?;
            let body_str = from_utf8(body).unwrap_or("").trim_matches(char::from(0));
            let updated_student: Student  = serde_json::from_str(body_str)?;

            if updated_student.id != student_id {
                return Ok(build_response("400 Bad Request", "{\"error\": \"ID mismatch between URL and body\"}"));
            }            

            {
                let mut db_guard = db.write().map_err(|e| anyhow::anyhow!("Lock poisoned: {e}"))?;
                if let Some(index) = db_guard.students.iter().position(|s| s.id == student_id) {
                    db_guard.students[index] = updated_student;
                    let json = serde_json::to_string_pretty(&*db_guard)?;
                    fs::write("database.json", json)?;                    
                } else {
                    return Ok(build_response("404 Not found", "{\"error\": \"Student doesn't exist\"}"));
                }

            }
            Ok(build_response("200 OK", "{\"message\": \"Student updated\"}"))
        }

        ("PUT", ["students", student_id_str, "grades", class_id_str]) => {
            let student_id = Uuid::parse_str(*student_id_str)?;
            let class_id = Uuid::parse_str(*class_id_str)?;

            #[derive(serde::Deserialize)]
            struct UpdateScoreRequest {
                score: u8
            }

            let body_str = from_utf8(body).unwrap_or("").trim_matches(char::from(0));
            
            let payload: UpdateScoreRequest = serde_json::from_str(body_str)?; 
            let updated_score = payload.score;
            
            if updated_score > 10 {
                return Ok(build_response("400 Bad request", "{\"error\": \"Invalid score value\"}"));
            }
            {
                let mut db_guard = db.write().map_err(|e| anyhow::anyhow!("Lock poisoned: {e}"))?;
                if let Some(index) = db_guard.student_classes.iter().position(|sc| sc.class_id == class_id && sc.student_id == student_id) {
                    db_guard.student_classes[index].score = updated_score;

                    let json = serde_json::to_string_pretty(&*db_guard)?;
                    std::fs::write("database.json", json)?;
                } else {
                    return Ok(build_response("404 Not found", "{\"error\": \"Student doesn't exist\"}"));                    
                }
            }

            Ok(build_response("200 OK", "{\"message\": \"Score has been succesfully updated\"}"))
        }
        

        ("DELETE", ["students"]) => {
            return Ok(build_response("405 Not Allowed", "{\"error\": \"Cannot delete entire collection\"}"));
        }

        ("DELETE", ["students", student_id_str, "grades", class_id_str]) => {
            let student_id = Uuid::parse_str(*student_id_str)?;
            let class_id = Uuid::parse_str(*class_id_str)?;
            {
                let mut db_guard = db.write().map_err(|e|anyhow!("Lock poisoned: {e}"))?;
                
                if let Some(index) = db_guard.student_classes.iter().position(|sc| sc.class_id == class_id && sc.student_id == student_id) {
                    db_guard.student_classes.swap_remove(index);
                    let json = serde_json::to_string_pretty(&*db_guard)?;
                    fs::write("database.json", json)?;
                    return Ok(build_response("200 OK", "{\"message\": \"Grade has successfully been deleted!\"}"));
                } else {
                    return Ok(build_response("404 Not Found", "{\"error\": \"Student doesn't have a score for this class\"}"));
                }

            }
        }

        ("PATCH", ["students", student_id_str]) => {
            let student_id = Uuid::parse_str(*student_id_str)?;
            #[derive(Deserialize)]
            struct PatchStudentRequest {
                name: Option<String>,
                gender: Option<Gender>
            }

            let body_str = from_utf8(body).unwrap_or("").trim_matches(char::from(0));
            let payload: PatchStudentRequest = serde_json::from_str(body_str)?;

            {
                let mut db_guard = db.write().map_err(|e| anyhow!("Lock poisoned: {e}"))?;

                if let Some(index) = db_guard.students.iter().position(|s| s.id == student_id) {
                    if let Some(new_name) = payload.name {
                        db_guard.students[index].name = new_name;
                }

                    if let Some(new_gender) = payload.gender {
                        db_guard.students[index].gender = new_gender;
                    }   
                    let json = serde_json::to_string_pretty(&*db_guard)?;
                    std::fs::write("database.json", json)?;
                } else {
                    return Ok(build_response("404 Not Found", "{\"error\": \"Student doesn't exist\"}"));
                }
            }

            Ok(build_response("200 OK", "{\"message\": \"Student successfully updated\"}"))
        }

        ("PATCH", ["classes", class_id_str]) => {
            let class_id = uuid::Uuid::parse_str(class_id_str)?;
            #[derive(serde::Deserialize)]
            struct PatchClassRequest {
                teacher_id: Option<uuid::Uuid>,
                end_period: Option<chrono::NaiveDate>, 
            }            

            let body_str = from_utf8(body).unwrap_or("").trim_matches(char::from(0));
            let payload: PatchClassRequest = serde_json::from_str(body_str)?;

            {
                let mut db_guard = db.write().map_err(|e| anyhow::anyhow!("Lock poisoned: {e}"))?;

                if let Some(index) = db_guard.classes.iter().position(|c| c.id == class_id) {
                    if let Some(new_teacher_id) = payload.teacher_id {
                        if db_guard.teachers.iter().any(|t| t.id == new_teacher_id) {
                            db_guard.classes[index].teacher_id = new_teacher_id;
                        } else {
                            return Ok(build_response("404 Not Found", "{\"error\": \"New teacher doesn't exist\"}"));
                        }
                    }

                    if let Some(new_end_period) = payload.end_period {
                        db_guard.classes[index].end_period  = new_end_period;
                    }

                    let json = serde_json::to_string_pretty(&*db_guard)?;
                    fs::write("database.json", json)?;
                } else {
                    return Ok(build_response("404 Not Found", "{\"error\": \"Class doesn't exist\"}"));
                }
            }
            Ok(build_response("200 OK", "{\"message\": \"Class successfully updated\"}"))
        }
        _ => {
            Ok(build_response("404 Not Found", "{\"error\": \"Endpoint not found\"}"))
        }
    }

}

fn handle_connection(mut stream: TcpStream, db: Arc<RwLock<SchoolDatabase>>) -> anyhow::Result<()> {
    let mut buf = [0; 4096];
    let bytes_read = stream.read(&mut buf)?;
    if bytes_read == 0 {
        return Ok(());
    }
    let mut headers = [httparse::EMPTY_HEADER; 16];
    let mut req = httparse::Request::new(&mut headers);
    match req.parse(&buf) {
        Ok(httparse::Status::Complete(body_offset)) => {
            let method = req.method.unwrap_or("GET");
            let path = req.path.unwrap_or("/");
            let response = match handle_request(method, path, &buf[body_offset..bytes_read], db) {
                Ok(r) => r,
                Err(e) => {
                    eprintln!("Error: {} {}: {}", method, path, e);
                    build_response("400 Bad request", "{\"error\": \"Invalid data\"}")
                }
            };
            stream.write_all(response.as_bytes())?;
            stream.flush()?;
        }

        Ok(httparse::Status::Partial) => {
            eprintln!("Buffer is too large!");
            let error_response = build_response("413 Payload Too Large", "{\"error\": \"Request too large\"}");
            stream.write_all(error_response.as_bytes())?;        }

        Err(e) => {
            eprintln!("Error at parsing HTTP request: {}", e);
            let error_response = build_response("400 Bad Request", "{\"error\": \"Invalid HTTP Request\"}");
            stream.write_all(error_response.as_bytes())?;        }
    }
    Ok(())
}





fn main() -> anyhow::Result<()> {    
    let db_path =  Path::new("database.json");
    let db: SchoolDatabase = if fs::metadata(db_path).map(|m| m.len()).unwrap_or(0) == 0 {
        seed_database(db_path)
    } else {
        let data = fs::read_to_string(db_path)?;
        serde_json::from_str(&data).expect("Error ar parsing json")
    };

                            
    let db = Arc::new(RwLock::new(db));
    let listener = TcpListener::bind("127.0.0.1:8080")?;
    let addr = listener.local_addr()?;
    println!("Server started on address: {}", addr);
    for stream in listener.incoming() {
        match stream {
            Ok(stream) => {
                let db_clone = db.clone();
                thread::spawn(move || {
                    if let Err(e) = handle_connection(stream, db_clone) {
                        eprintln!("Connection Error: {}", e);
                    }
                });
            }
            
            Err(e) => {
                eprintln!("Connection failed: {e}");
            }
        }
    }
    Ok(())
}
