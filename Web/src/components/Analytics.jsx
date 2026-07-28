import React from 'react';
import { LineChart, Line, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { TrendingUp, Activity } from 'lucide-react';

const mockStrengthData = [
  { month: 'Jan', weight: 120 },
  { month: 'Feb', weight: 135 },
  { month: 'Mar', weight: 140 },
  { month: 'Apr', weight: 155 },
  { month: 'May', weight: 170 },
  { month: 'Jun', weight: 185 },
];

const mockVolumeData = [
  { day: 'Mon', volume: 4500 },
  { day: 'Tue', volume: 0 },
  { day: 'Wed', volume: 5200 },
  { day: 'Thu', volume: 0 },
  { day: 'Fri', volume: 3800 },
  { day: 'Sat', volume: 6000 },
  { day: 'Sun', volume: 0 },
];

const CustomTooltip = ({ active, payload, label }) => {
  if (active && payload && payload.length) {
    return (
      <div className="glass-panel" style={{ padding: '1rem', border: '1px solid rgba(139, 92, 246, 0.5)' }}>
        <p style={{ margin: '0 0 0.5rem 0', fontWeight: 'bold' }}>{label}</p>
        <p style={{ margin: 0, color: '#8b5cf6' }}>
          {payload[0].value} kg
        </p>
      </div>
    );
  }
  return null;
};

export default function Analytics() {
  return (
    <div className="animate-fade-in">
      <h1 style={{ fontSize: '2.5rem', marginBottom: '2rem' }}>Analytics & Progress</h1>
      
      <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '2rem' }}>
        {/* Strength Progression (1RM) */}
        <div className="glass-panel" style={{ padding: '2rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '2rem' }}>
            <div style={{ background: 'rgba(139, 92, 246, 0.2)', padding: '0.75rem', borderRadius: '50%', color: '#8b5cf6' }}>
              <TrendingUp size={24} />
            </div>
            <div>
              <h2 style={{ margin: 0, fontSize: '1.25rem' }}>Estimated 1-Rep Max</h2>
              <div style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Deadlift Progression (Last 6 Months)</div>
            </div>
          </div>
          
          <div style={{ height: '300px', width: '100%' }}>
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={mockStrengthData}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.1)" vertical={false} />
                <XAxis dataKey="month" stroke="var(--text-muted)" tick={{fill: 'var(--text-muted)'}} axisLine={false} tickLine={false} />
                <YAxis stroke="var(--text-muted)" tick={{fill: 'var(--text-muted)'}} axisLine={false} tickLine={false} />
                <Tooltip content={<CustomTooltip />} />
                <Line 
                  type="monotone" 
                  dataKey="weight" 
                  stroke="#8b5cf6" 
                  strokeWidth={4}
                  dot={{ fill: '#8b5cf6', strokeWidth: 2, r: 6, stroke: '#1e293b' }}
                  activeDot={{ r: 8, strokeWidth: 0 }}
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Volume Load */}
        <div className="glass-panel" style={{ padding: '2rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '2rem' }}>
            <div style={{ background: 'rgba(59, 130, 246, 0.2)', padding: '0.75rem', borderRadius: '50%', color: '#3b82f6' }}>
              <Activity size={24} />
            </div>
            <div>
              <h2 style={{ margin: 0, fontSize: '1.25rem' }}>Weekly Volume Load</h2>
              <div style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Total weight lifted this week</div>
            </div>
          </div>
          
          <div style={{ height: '300px', width: '100%' }}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={mockVolumeData}>
                <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.1)" vertical={false} />
                <XAxis dataKey="day" stroke="var(--text-muted)" tick={{fill: 'var(--text-muted)'}} axisLine={false} tickLine={false} />
                <YAxis stroke="var(--text-muted)" tick={{fill: 'var(--text-muted)'}} axisLine={false} tickLine={false} />
                <Tooltip content={<CustomTooltip />} cursor={{fill: 'rgba(255,255,255,0.05)'}} />
                <Bar dataKey="volume" fill="#3b82f6" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    </div>
  );
}
