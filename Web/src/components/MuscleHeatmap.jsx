import React from 'react';

export default function MuscleHeatmap({ viewMode, getMuscleColor, onMuscleClick, className = '' }) {
  const Dark300 = '#374151'; // matches the Dark300 in Android theme

  const handleClick = (muscleName) => {
    if (onMuscleClick) {
      onMuscleClick(muscleName);
    }
  };

  return (
    <div className={`muscle-heatmap-container ${className}`} style={{ width: '100%', height: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
      <svg viewBox="0 0 200 400" preserveAspectRatio="xMidYMid meet" style={{ width: '100%', height: '100%', cursor: 'pointer' }}>
        {viewMode === "front" ? (
          <g>
            {/* Head & Neck */}
            <circle cx="100" cy="30" r="18" fill={Dark300} />
            <rect x="92" y="45" width="16" height="15" fill={Dark300} />

            {/* Front Delts */}
            <path d="M 65 60 Q 75 55 92 60 L 92 80 L 60 80 Z" fill={getMuscleColor("Front Delts")} onClick={() => handleClick("Front Delts")} />
            <path d="M 135 60 Q 125 55 108 60 L 108 80 L 140 80 Z" fill={getMuscleColor("Front Delts")} onClick={() => handleClick("Front Delts")} />

            {/* Chest */}
            <path d="M 70 80 L 130 80 L 130 115 Q 100 125 70 115 Z" fill={getMuscleColor("Chest")} onClick={() => handleClick("Chest")} />

            {/* Abs */}
            <rect x="80" y="120" width="40" height="50" rx="4" fill={getMuscleColor("Abs")} onClick={() => handleClick("Abs")} />

            {/* Obliques */}
            <path d="M 70 120 L 78 120 L 78 170 L 65 160 Z" fill={getMuscleColor("Obliques")} onClick={() => handleClick("Obliques")} />
            <path d="M 130 120 L 122 120 L 122 170 L 135 160 Z" fill={getMuscleColor("Obliques")} onClick={() => handleClick("Obliques")} />

            {/* Biceps */}
            <rect x="50" y="85" width="18" height="40" rx="8" fill={getMuscleColor("Biceps")} onClick={() => handleClick("Biceps")} />
            <rect x="132" y="85" width="18" height="40" rx="8" fill={getMuscleColor("Biceps")} onClick={() => handleClick("Biceps")} />

            {/* Forearms */}
            <rect x="45" y="130" width="14" height="45" rx="6" fill={getMuscleColor("Forearms")} onClick={() => handleClick("Forearms")} />
            <rect x="141" y="130" width="14" height="45" rx="6" fill={getMuscleColor("Forearms")} onClick={() => handleClick("Forearms")} />

            {/* Pelvis */}
            <path d="M 65 175 L 135 175 L 120 200 L 80 200 Z" fill={Dark300} />

            {/* Quads */}
            <rect x="65" y="205" width="30" height="75" rx="10" fill={getMuscleColor("Quads")} onClick={() => handleClick("Quads")} />
            <rect x="105" y="205" width="30" height="75" rx="10" fill={getMuscleColor("Quads")} onClick={() => handleClick("Quads")} />

            {/* Calves */}
            <rect x="68" y="290" width="24" height="60" rx="8" fill={getMuscleColor("Calves")} onClick={() => handleClick("Calves")} />
            <rect x="108" y="290" width="24" height="60" rx="8" fill={getMuscleColor("Calves")} onClick={() => handleClick("Calves")} />
          </g>
        ) : (
          <g>
            {/* Head & Neck */}
            <circle cx="100" cy="30" r="18" fill={Dark300} />
            <rect x="92" y="45" width="16" height="15" fill={Dark300} />

            {/* Upper Back */}
            <path d="M 75 55 L 125 55 L 110 90 L 90 90 Z" fill={getMuscleColor("Upper Back")} onClick={() => handleClick("Upper Back")} />

            {/* Rear Delts */}
            <path d="M 55 60 Q 65 55 75 60 L 75 80 L 50 80 Z" fill={getMuscleColor("Rear Delts")} onClick={() => handleClick("Rear Delts")} />
            <path d="M 145 60 Q 135 55 125 60 L 125 80 L 150 80 Z" fill={getMuscleColor("Rear Delts")} onClick={() => handleClick("Rear Delts")} />

            {/* Lats */}
            <path d="M 65 85 L 95 95 L 95 150 L 70 120 Z" fill={getMuscleColor("Lats")} onClick={() => handleClick("Lats")} />
            <path d="M 135 85 L 105 95 L 105 150 L 130 120 Z" fill={getMuscleColor("Lats")} onClick={() => handleClick("Lats")} />

            {/* Lower Back */}
            <rect x="85" y="155" width="30" height="20" fill={Dark300} />

            {/* Triceps */}
            <rect x="48" y="85" width="16" height="42" rx="8" fill={getMuscleColor("Triceps")} onClick={() => handleClick("Triceps")} />
            <rect x="136" y="85" width="16" height="42" rx="8" fill={getMuscleColor("Triceps")} onClick={() => handleClick("Triceps")} />

            {/* Forearms */}
            <rect x="42" y="132" width="14" height="45" rx="6" fill={getMuscleColor("Forearms")} onClick={() => handleClick("Forearms")} />
            <rect x="144" y="132" width="14" height="45" rx="6" fill={getMuscleColor("Forearms")} onClick={() => handleClick("Forearms")} />

            {/* Glutes */}
            <path d="M 60 180 L 100 180 L 100 220 Q 80 230 60 210 Z" fill={getMuscleColor("Glutes")} onClick={() => handleClick("Glutes")} />
            <path d="M 140 180 L 100 180 L 100 220 Q 120 230 140 210 Z" fill={getMuscleColor("Glutes")} onClick={() => handleClick("Glutes")} />

            {/* Hamstrings */}
            <rect x="65" y="225" width="28" height="65" rx="10" fill={getMuscleColor("Hamstrings")} onClick={() => handleClick("Hamstrings")} />
            <rect x="107" y="225" width="28" height="65" rx="10" fill={getMuscleColor("Hamstrings")} onClick={() => handleClick("Hamstrings")} />

            {/* Calves */}
            <rect x="68" y="295" width="24" height="60" rx="8" fill={getMuscleColor("Calves")} onClick={() => handleClick("Calves")} />
            <rect x="108" y="295" width="24" height="60" rx="8" fill={getMuscleColor("Calves")} onClick={() => handleClick("Calves")} />
          </g>
        )}
      </svg>
    </div>
  );
}
