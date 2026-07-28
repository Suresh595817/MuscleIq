import React from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import Dashboard from './components/Dashboard';
import Analytics from './components/Analytics';
import AIWorkout from './components/AIWorkout';
import WorkoutTracker from './components/WorkoutTracker';
import './index.css';

function App() {
  return (
    <BrowserRouter>
      <div className="app-container" style={{ padding: '2rem', maxWidth: '1200px', margin: '0 auto' }}>
        <nav className="glass-panel" style={{ padding: '1rem 2rem', marginBottom: '2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div style={{ fontSize: '1.5rem', fontWeight: 'bold', background: 'linear-gradient(to right, #8b5cf6, #3b82f6)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
            MuscleIQ
          </div>
          <div style={{ display: 'flex', gap: '1.5rem' }}>
            <Link to="/" style={{ color: 'var(--text-main)', textDecoration: 'none', fontWeight: 500 }}>Dashboard</Link>
            <Link to="/track" style={{ color: 'var(--text-main)', textDecoration: 'none', fontWeight: 500 }}>Tracker</Link>
            <Link to="/ai" style={{ color: 'var(--text-main)', textDecoration: 'none', fontWeight: 500 }}>AI Generator</Link>
            <Link to="/analytics" style={{ color: 'var(--text-main)', textDecoration: 'none', fontWeight: 500 }}>Analytics</Link>
          </div>
        </nav>

        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/track" element={<WorkoutTracker />} />
          <Route path="/ai" element={<AIWorkout />} />
          <Route path="/analytics" element={<Analytics />} />
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App;
