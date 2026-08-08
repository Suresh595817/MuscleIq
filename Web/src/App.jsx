import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { onAuthStateChanged } from 'firebase/auth';
import { auth } from './lib/firebase';

// Components
import Sidebar from './components/Sidebar';
import Dashboard from './components/Dashboard';
import Analytics from './components/Analytics';
import AIWorkout from './components/AIWorkout';
import AiDiet from './components/AiDiet';
import WorkoutTracker from './components/WorkoutTracker';
import MuscleMap from './components/MuscleMap';
import SelectMuscleGroup from './components/SelectMuscleGroup';
import Auth from './components/Auth';

// New Components
import Profile from './components/Profile';
import History from './components/History';

import './index.css';

function App() {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (currentUser) => {
      setUser(currentUser);
      setLoading(false);
    });
    return () => unsubscribe();
  }, []);

  if (loading) {
    return <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', color: 'white' }}>Loading...</div>;
  }

  return (
    <BrowserRouter basename="/MuscleIq/">
      <div style={{ display: 'flex', minHeight: '100vh', backgroundColor: '#09090b' }}>
        
        {/* Persistent Sidebar (Only visible when logged in) */}
        {user && <Sidebar />}

        {/* Main Content Area */}
        <main style={{ 
          flex: 1, 
          marginLeft: user ? '260px' : '0', 
          padding: '2rem',
          maxWidth: '1200px',
          margin: user ? '0 auto 0 260px' : '0 auto',
          width: '100%',
          boxSizing: 'border-box'
        }}>
          <Routes>
            {!user ? (
              <>
                <Route path="/login" element={<Auth />} />
                <Route path="*" element={<Navigate to="/login" replace />} />
              </>
            ) : (
              <>
                {/* Primary Nav Routes */}
                <Route path="/" element={<Dashboard />} />
                <Route path="/track" element={<WorkoutTracker />} />
                <Route path="/history" element={<History />} />
                <Route path="/profile" element={<Profile />} />

                {/* Secondary Feature Routes */}
                <Route path="/select-muscle" element={<SelectMuscleGroup />} />
                <Route path="/musclemap" element={<MuscleMap />} />
                <Route path="/ai" element={<AIWorkout />} />
                <Route path="/diet" element={<AiDiet />} />
                <Route path="/analytics" element={<Analytics />} />
                
                <Route path="*" element={<Navigate to="/" replace />} />
              </>
            )}
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App;
