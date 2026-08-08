import React from 'react';
import { signOut } from 'firebase/auth';
import { auth } from '../lib/firebase';
import { User, Bell, Settings, CreditCard, LogOut, ChevronRight, Camera } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function Profile() {
  const navigate = useNavigate();
  const userName = auth.currentUser?.displayName || "Athlete";
  const userEmail = auth.currentUser?.email || "";

  const handleLogout = async () => {
    try {
      await signOut(auth);
      navigate('/login');
    } catch (error) {
      console.error("Error logging out:", error);
    }
  };

  const menuItems = [
    { icon: <Bell size={24} />, title: "Notifications", subtitle: "Workout reminders & updates" },
    { icon: <Settings size={24} />, title: "Account Settings", subtitle: "Password & security" }
  ];

  return (
    <div className="animate-fade-in" style={{ paddingBottom: '2rem' }}>
      
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '2rem', fontWeight: 'bold', color: 'white' }}>Profile</h1>
      </div>

      {/* Profile Header Card */}
      <div className="glass-panel" style={{ padding: '2rem', display: 'flex', alignItems: 'center', gap: '1.5rem', marginBottom: '2rem' }}>
        <div style={{ position: 'relative' }}>
          <div style={{ 
            width: '100px', height: '100px', borderRadius: '50%', backgroundColor: '#27272a',
            display: 'flex', alignItems: 'center', justifyContent: 'center', border: '3px solid #3b82f6'
          }}>
            <User size={48} color="#a1a1aa" />
          </div>
          <button style={{
            position: 'absolute', bottom: '0', right: '0', background: '#3b82f6', border: 'none',
            borderRadius: '50%', width: '32px', height: '32px', display: 'flex', alignItems: 'center', justifyContent: 'center',
            cursor: 'pointer'
          }}>
            <Camera size={16} color="white" />
          </button>
        </div>

        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 'bold', color: 'white', marginBottom: '0.25rem' }}>{userName}</h2>
          <p style={{ color: '#a1a1aa' }}>{userEmail}</p>
          <div style={{ display: 'inline-block', padding: '0.25rem 0.75rem', background: 'rgba(59, 130, 246, 0.2)', color: '#3b82f6', borderRadius: '1rem', marginTop: '0.5rem', fontSize: '0.875rem', fontWeight: 'bold' }}>
            PRO MEMBER
          </div>
        </div>
      </div>

      {/* Menu Options */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', marginBottom: '2rem' }}>
        <h3 style={{ fontSize: '1.25rem', fontWeight: 'bold', color: 'white', marginBottom: '0.5rem' }}>Settings</h3>
        
        {menuItems.map((item, index) => (
          <button key={index} className="glass-panel" style={{ 
            width: '100%', padding: '1.25rem 1.5rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            background: '#18181b', border: '1px solid #27272a', borderRadius: '1rem', cursor: 'pointer', textAlign: 'left',
            transition: 'background 0.2s'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
              <div style={{ color: '#3b82f6' }}>{item.icon}</div>
              <div>
                <div style={{ color: 'white', fontWeight: '600', fontSize: '1.1rem' }}>{item.title}</div>
                <div style={{ color: '#a1a1aa', fontSize: '0.9rem', marginTop: '0.25rem' }}>{item.subtitle}</div>
              </div>
            </div>
            <ChevronRight size={24} color="#a1a1aa" />
          </button>
        ))}
      </div>

      {/* Logout Button */}
      <button 
        onClick={handleLogout}
        className="glass-panel" 
        style={{ 
          width: '100%', padding: '1.25rem 1.5rem', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.75rem',
          background: 'rgba(239, 68, 68, 0.1)', border: '1px solid #ef4444', borderRadius: '1rem', cursor: 'pointer',
          color: '#ef4444', fontWeight: 'bold', fontSize: '1.1rem', transition: 'background 0.2s'
        }}
      >
        <LogOut size={20} />
        Logout
      </button>

    </div>
  );
}
