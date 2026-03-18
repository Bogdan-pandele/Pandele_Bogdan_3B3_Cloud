import { useEffect, useState } from 'react'

export default function Dashboard() {
  const [data, setData] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetch('http://localhost:3000/api/dashboard')
      .then(res => {
        if (!res.ok) throw new Error("Connection failed");
        return res.json();
      })
      .then(data => setData(data))
      .catch(err => setError(err.message));
  }, []);

  if (error) return <div className="p-10 text-red-500 font-medium text-center">Error: {error}</div>;
  if (!data) return <div className="p-10 text-gray-400 text-center animate-pulse">Loading dashboard...</div>;

  return (
    <div className="font-sans antialiased text-gray-900">

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        <div className="bg-slate-50 p-6 rounded-xl">
          <h2 className="text-xs font-semibold text-slate-500 uppercase tracking-tight mb-1">Total Students</h2>
          <p className="text-4xl font-bold text-slate-900">{data.total_students}</p>
        </div>

        <div className="bg-slate-50 p-6 rounded-xl">
          <h2 className="text-xs font-semibold text-slate-500 uppercase tracking-tight mb-1">Bucharest Weather</h2>
          <p className="text-lg font-medium text-slate-800">{data.weather}</p>
        </div>
      </div>

      <div className="bg-slate-50 p-6 rounded-xl">
        <h2 className="text-xs font-semibold text-slate-500 uppercase tracking-tight mb-4">Upcoming Holidays</h2>
        <ul className="divide-y divide-slate-200">
          {data.next_holidays.map((h: any, index: number) => (
            <div key={index} className="py-3 first:pt-0 last:pb-0">
              <span className="text-xs font-bold text-slate-400 mr-3">{h.date}</span>
              <span className="text-sm font-medium">{h.localName}</span>
              <span className="text-xs text-slate-400 ml-2">({h.name})</span>
            </div>
          ))}
        </ul>
      </div>
    </div>
  )
}