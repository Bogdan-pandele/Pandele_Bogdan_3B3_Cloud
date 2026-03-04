use std::{fs::{OpenOptions}, io::Write, path::{Path}};

use chrono::{NaiveDate};
use serde::{Deserialize, Serialize};
use uuid::Uuid;



#[derive(Serialize, Deserialize, Debug)]
pub enum Gender{
    Male,
    Female
}

#[derive(Serialize, Deserialize, Debug)]
pub struct Student {
    pub id: Uuid,
    pub name: String,
    pub date_of_birth: NaiveDate,
    pub gender: Gender,
    pub enrolement_date: NaiveDate
}
impl Default for Student {
    fn default() -> Self {
        Student { 
            id: Uuid::new_v4(), 
            name: "".to_string(),
            date_of_birth: NaiveDate::default(), 
            gender: Gender::Male, 
            enrolement_date: NaiveDate::default() 
        }
    }
}

#[derive(Serialize, Deserialize, Default, Debug)]
pub struct Subject {
    id: Uuid,
    department_id: Uuid,
    subject_name: String
}

#[derive(Serialize, Deserialize, Default, Debug)]
pub struct StudentClass {
    pub student_id: Uuid,
    pub class_id: Uuid,
    pub score: u8
}

#[derive(Serialize, Deserialize, Default, Debug)]
pub struct Department {
    id: Uuid,
    name: String
}

#[derive(Serialize, Deserialize, Default, Debug)]
pub struct SchoolYear {
    id: Uuid,
    start_date: NaiveDate,
    end_date: NaiveDate,
    year: u16
}

#[derive(Serialize, Deserialize, Default, Debug)]
pub struct StudentYear {
    pub student_id: Uuid,
    pub year_id: Uuid,
    pub average_score: u8
}

#[derive(Serialize, Deserialize, Default, Debug)]
pub struct Class {
    pub id: Uuid,
    pub subject_id: Uuid,
    pub teacher_id: Uuid,
    pub start_period: NaiveDate,
    pub end_period: NaiveDate,
    pub school_year_id: Uuid
}

#[derive(Serialize, Deserialize, Debug)]
pub struct Teacher {
    pub id: Uuid,
    pub name: String,
    pub gender: Gender,
}

impl Default for Teacher {
    fn default() -> Self {
        Teacher { id: Uuid::new_v4(), name: "".to_string(), gender: Gender::Male }
    }
}
#[derive(Serialize, Deserialize, Debug, Default)]
pub struct SchoolDatabase {
    pub students: Vec<Student>,
    pub teachers: Vec<Teacher>,
    pub departments: Vec<Department>,
    pub subjects: Vec<Subject>,
    pub classes: Vec<Class>,
    pub school_years: Vec<SchoolYear>,
    pub student_classes: Vec<StudentClass>,
    pub student_years: Vec<StudentYear>,
}

pub fn seed_database(path: &Path) -> SchoolDatabase {
    let mut db = SchoolDatabase::default();
    
    let dep_science_id = Uuid::new_v4();
    db.departments.push(Department { id: dep_science_id, name: "Science".to_string() });

    let dep_math_id = Uuid::new_v4();
    db.departments.push(Department { id: dep_math_id, name: "Mathematics".to_string() });

    let teacher_elena_id = Uuid::new_v4();
    db.teachers.push(Teacher { id: teacher_elena_id, name: "Elena".to_string(), gender: Gender::Female });

    let teacher_florin_id = Uuid::new_v4();
    db.teachers.push(Teacher { id: teacher_florin_id, name: "Florin".to_string(), gender: Gender::Male });

    let subject_chem_id = Uuid::new_v4();
    db.subjects.push(Subject { id: subject_chem_id, department_id: dep_science_id, subject_name: "Chemistry".to_string() });

    let year_23_24_id = Uuid::new_v4();
    db.school_years.push(SchoolYear {
        id: year_23_24_id,
        start_date: NaiveDate::from_ymd_opt(2023, 9, 11).unwrap(),
        end_date: NaiveDate::from_ymd_opt(2024, 6, 21).unwrap(),
        year: 2023
    });

    let class_chem_id = Uuid::new_v4();
    db.classes.push(Class { 
        id: class_chem_id, 
        subject_id: subject_chem_id, 
        teacher_id: teacher_elena_id, 
        school_year_id: year_23_24_id,
        start_period: NaiveDate::from_ymd_opt(2023, 9, 11).unwrap(), 
        end_period: NaiveDate::from_ymd_opt(2024, 6, 21).unwrap()
    });

    let student_marcel_id = Uuid::new_v4();
    db.students.push(Student { 
        id: student_marcel_id, 
        name: "Marcel".to_string(), 
        date_of_birth: NaiveDate::from_ymd_opt(2004, 5, 13).unwrap(), 
        gender: Gender::Male, 
        enrolement_date: NaiveDate::from_ymd_opt(2022, 9, 1).unwrap() 
    });

    let student_ioana_id = Uuid::new_v4();
    db.students.push(Student { 
        id: student_ioana_id, 
        name: "Ioana".to_string(), 
        date_of_birth: NaiveDate::from_ymd_opt(2005, 8, 20).unwrap(), 
        gender: Gender::Female, 
        enrolement_date: NaiveDate::from_ymd_opt(2023, 9, 1).unwrap() 
    });

    let student_vasile_id = Uuid::new_v4();
    db.students.push(Student { id: student_vasile_id, 
        name: "Vasile".to_string(),
        date_of_birth: NaiveDate::from_ymd_opt(2005, 5, 3).unwrap(), 
        gender: Gender::Male, 
        enrolement_date: NaiveDate::from_ymd_opt(2024, 9, 1).unwrap() 
    });
    db.student_classes.push(StudentClass {
        student_id: student_ioana_id,
        class_id: class_chem_id,
        score: 10 
    });

    db.student_classes.push(StudentClass { student_id: student_marcel_id, class_id: class_chem_id, score: 9 });
    db.student_years.push(StudentYear { student_id: student_marcel_id, year_id: year_23_24_id, average_score: 9 });

    let mut file = OpenOptions::new()
            .write(true)
            .create(true)
            .truncate(true)
            .open(path)
            .unwrap_or_else(|e| panic!("Error at opening file: {e}"));

    let json_data = serde_json::to_string_pretty(&db).expect("Error at serialization");
    file.write_all(json_data.as_bytes()).expect("Error at writing json");    
    
    db
}