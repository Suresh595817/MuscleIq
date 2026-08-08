import React from 'react';
import { NavLink } from 'react-router-dom';
import { Home, Dumbbell, History, User } from 'lucide-react';
import muscleiqLogo from '../assets/muscleiq_logo.jpg';

export default function Sidebar() {
  const navItems = [
    { name: 'Dashboard', path: '/', icon: <Home size={24} /> },
    { name: 'Workout', path: '/track', icon: <Dumbbell size={24} /> },
    { name: 'History', path: '/history', icon: <History size={24} /> },
    { name: 'Profile', path: '/profile', icon: <User size={24} /> },
  ];

  return (
    <aside style={{
      width: '260px',
      height: '100vh',
      backgroundColor: '#18181b', // Dark100 equivalent
      borderRight: '1px solid #27272a',
      padding: '2rem 1rem',
      display: 'flex',
      flexDirection: 'column',
      position: 'fixed',
      top: 0,
      left: 0,
      overflowY: 'auto'
    }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '3rem', paddingLeft: '1rem' }}>
        <img 
          src={muscleiqLogo} 
          alt="MuscleIQ" 
          style={{ width: '40px', height: '40px', borderRadius: '12px', border: '2px solid #8b5cf6', objectFit: 'cover' }} 
        />
        <div style={{ fontSize: '1.5rem', fontWeight: 'bold', color: 'white' }}>
          MuscleIQ
        </div>
      </div>

      <nav style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
        {navItems.map((item) => (
          <NavLink
            key={item.name}
            to={item.path}
            style={({ isActive }) => ({
              display: 'flex',
              alignItems: 'center',
              gap: '1rem',
              padding: '1rem',
              borderRadius: '0.75rem',
              textDecoration: 'none',
              color: isActive ? '#3b82f6' : '#a1a1aa', // Accent color when active
              backgroundColor: isActive ? 'rgba(59, 130, 246, 0.1)' : 'transparent',
              fontWeight: isActive ? '600' : '500',
              transition: 'all 0.2s ease-in-out'
            })}
          >
            {item.icon}
            <span style={{ fontSize: '1.1rem' }}>{item.name}</span>
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
