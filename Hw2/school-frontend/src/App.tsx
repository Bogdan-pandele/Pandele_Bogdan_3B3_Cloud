import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import StudentsPage from './pages/StudentPage';
import Dashboard from './pages/Dashboard';

function App() {
  return (
    <BrowserRouter>
      <div className="min-h-screen bg-slate-50 font-sans text-slate-900">

        <nav className="bg-slate-900 text-white p-4 shadow-sm">
          <div className="max-w-5xl mx-auto flex gap-6 font-medium text-sm tracking-wide">
            <Link to="/" className="hover:text-blue-400 transition-colors">Dashboard</Link>
            <Link to="/students" className="hover:text-blue-400 transition-colors">Students</Link>
          </div>
        </nav>

        <main className="max-w-5xl mx-auto p-8">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/students" element={<StudentsPage />} />
            <Route path="/students/:id" element={<h1 className="text-2xl font-bold">Single Student Details coming soon...</h1>} />
          </Routes>
        </main>

      </div>
    </BrowserRouter>
  );
}

export default App;