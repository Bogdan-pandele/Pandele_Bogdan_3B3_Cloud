import { useEffect, useState } from 'react';

interface Student {
  id: string;
  name: string;
  date_of_birth: string;
  gender: string;
  enrolement_date: string;
}

export default function StudentsPage() {
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);

  const [newStudent, setNewStudent] = useState({
    name: '',
    date_of_birth: '',
    gender: 'Male',
    enrolement_date: new Date().toISOString().split('T')[0]
  });

  const fetchStudents = () => {
    setLoading(true);
    fetch('http://localhost:3000/api/students')
      .then(res => {
        if (!res.ok) throw new Error('Failed to load students');
        return res.json();
      })
      .then(data => {
        setStudents(data);
        setLoading(false);
      })
      .catch(err => {
        setError(err.message);
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchStudents();
  }, []);

const handleDeleteStudent = (id: string) => {
    const isConfirm = window.confirm("Are you sure you want to delete this student?")
    if(!isConfirm) return
    
    fetch(`http://localhost:3000/api/students/${id}`, {
      method: 'DELETE'
    })
    .then(res => {
      if(!res.ok) throw new Error('Failed to delete student');

      fetchStudents()
    })
    .catch(err => alert("Error at deleting student: " + err.message))
  };

const handleAddStudent = (e: React.FormEvent) => {
    e.preventDefault();

    const finalStudentData = {
      ...newStudent,
      id: crypto.randomUUID()
    };

    fetch('http://localhost:3000/api/students', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(finalStudentData)
    })
      .then(res => {
        if (!res.ok) throw new Error('Failed to add student');

        setNewStudent({
          name: '',
          date_of_birth: '',
          gender: 'Male',
          enrolement_date: new Date().toISOString().split('T')[0]
        });
        setShowForm(false);
        fetchStudents();
      })
      .catch(err => alert("Error adding student: " + err.message));
  };

  if (loading && students.length === 0) return <div className="p-10 text-slate-400 text-center animate-pulse">Loading students...</div>;
  if (error) return <div className="p-10 text-red-500 font-medium text-center">Error: {error}</div>;

  return (
    <div className="font-sans antialiased text-slate-900">

      <div className="flex justify-between items-center mb-8">
        <h1 className="text-2xl font-bold">Students Directory</h1>
        <button
          onClick={() => setShowForm(!showForm)}
          className="bg-slate-900 hover:bg-slate-800 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
        >
          {showForm ? 'Cancel' : '+ Add Student'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleAddStudent} className="bg-slate-50 p-6 rounded-xl mb-8 border border-slate-200">
          <h2 className="text-sm font-bold text-slate-800 mb-4">New Student Details</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-4">

            {/* Nume */}
            <input
              type="text"
              placeholder="Full Name"
              required
              value={newStudent.name}
              onChange={(e) => setNewStudent({...newStudent, name: e.target.value})}
              className="px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-500"
            />

            {/* Data Nașterii (Folosim type="date" ca să apară calendarul nativ) */}
            <input
              type="date"
              required
              value={newStudent.date_of_birth}
              onChange={(e) => setNewStudent({...newStudent, date_of_birth: e.target.value})}
              className="px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-500 text-slate-500"
            />

            {/* Gen (Dropdown) */}
            <select
              value={newStudent.gender}
              onChange={(e) => setNewStudent({...newStudent, gender: e.target.value})}
              className="px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-500 bg-white"
            >
              <option value="Male">Male</option>
              <option value="Female">Female</option>
            </select>

            <input
              type="date"
              required
              value={newStudent.enrolement_date}
              onChange={(e) => setNewStudent({...newStudent, enrolement_date: e.target.value})}
              className="px-4 py-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-slate-500 text-slate-500"
            />

          </div>
          <button type="submit" className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-2 rounded-lg text-sm font-medium transition-colors mt-2">
            Save Student
          </button>
        </form>
      )}

      {/* Tabelul Actualizat */}
      <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
        <table className="w-full text-left text-sm">
          <thead className="bg-slate-50 border-b border-slate-200 text-slate-500 uppercase text-xs font-semibold tracking-wider">
            <tr>
              <th className="px-6 py-4">Name</th>
              <th className="px-6 py-4">Date of Birth</th>
              <th className="px-6 py-4">Gender</th>
              <th className="px-6 py-4 text-right">Enrolled</th>
              <th className="px-6 py-4 text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {students.map((s) => (
              <tr key={s.id} className="hover:bg-slate-50 transition-colors">
                <td className="px-6 py-4 font-medium text-slate-800">{s.name}</td>
                <td className="px-6 py-4 text-slate-500">{s.date_of_birth}</td>
                <td className="px-6 py-4">
                  <span className="bg-slate-100 text-slate-600 px-2 py-1 rounded text-xs font-bold">{s.gender}</span>
                </td>
                <td className="px-6 py-4 text-right text-slate-400">{s.enrolement_date}</td>
                <td className="px-6 py-4 text-right">
                    <button onClick={() => handleDeleteStudent(s.id)}
                    className="text-red-500 hover:text-red-700 font-bold text-xs uppercase"
                    >
                    Delete
                    </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {students.length === 0 && (
          <div className="p-8 text-center text-slate-400">
            No students found. Click "Add Student" to create one.
          </div>
        )}
      </div>

    </div>
  );
}