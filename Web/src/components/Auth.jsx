import React, { useState } from 'react';
import { signInWithEmailAndPassword, createUserWithEmailAndPassword, sendEmailVerification, signOut, sendPasswordResetEmail, updateProfile, GoogleAuthProvider, signInWithPopup } from 'firebase/auth';
import { setDoc, doc } from 'firebase/firestore';
import { auth, db } from '../lib/firebase';
import { Mail, Lock, User, Phone, ArrowLeft } from 'lucide-react';
import muscleiqLogo from '../assets/muscleiq_logo.jpg';

export default function Auth() {
  const [authStep, setAuthStep] = useState("method"); // "method", "login", "register"
  
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isResetting, setIsResetting] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  
  // OTP States
  const [showOtpModal, setShowOtpModal] = useState(false);
  const [otpCode, setOtpCode] = useState("");
  const [otpLoading, setOtpLoading] = useState(false);

  const handleResetPassword = async (e) => {
    e.preventDefault();
    if (!email) {
      setError("Please enter your email address first.");
      return;
    }
    setLoading(true);
    setError("");
    try {
      await sendPasswordResetEmail(auth, email);
      setError("Password reset link sent! Please check your email.");
      setIsResetting(false);
    } catch (err) {
      setError(err.message);
    }
    setLoading(false);
  };

  const handleGoogleSignIn = async () => {
    setLoading(true);
    setError("");
    try {
      const provider = new GoogleAuthProvider();
      await signInWithPopup(auth, provider);
    } catch (err) {
      console.error(err);
      setError(err.message);
    }
    setLoading(false);
  };

  const handleSendOtp = async (e) => {
    e.preventDefault();
    if (!email || !password || !name) {
      setError("Please fill in all fields.");
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      setError("Please enter a valid email address.");
      return;
    }

    if (password.length < 6) {
      setError("Password must be at least 6 characters long.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const response = await fetch('http://localhost:5000/api/auth/send-otp', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email })
      });
      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || 'Failed to send OTP');
      }

      setShowOtpModal(true);
    } catch (err) {
      console.error(err);
      setError(err.message);
    }
    setLoading(false);
  };

  const handleVerifyOtp = async (e) => {
    e.preventDefault();
    if (!otpCode) {
      setError("Please enter the OTP.");
      return;
    }

    setOtpLoading(true);
    setError("");

    try {
      const response = await fetch('http://localhost:5000/api/auth/verify-otp', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, code: otpCode })
      });
      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || 'Invalid OTP');
      }

      // OTP Verified! Now create the Firebase account
        const userCredential = await createUserWithEmailAndPassword(auth, email, password);
        try {
          await updateProfile(auth.currentUser, { displayName: name });
          await setDoc(doc(db, 'Users', userCredential.user.uid), {
            id: userCredential.user.uid,
            name: name,
            email: email,
            createdAt: new Date().toISOString()
          });
        } catch (e) {
          console.error("Failed to update profile", e);
        }
      
      setShowOtpModal(false);
    } catch (err) {
      console.error(err);
      setError(err.message);
    }
    setOtpLoading(false);
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    if (!email || !password) {
      setError("Please fill in all fields.");
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      setError("Please enter a valid email address.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      await signInWithEmailAndPassword(auth, email, password);
    } catch (err) {
      console.error(err);
      setError(err.message);
    }
    setLoading(false);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '100vh', background: '#09090b', padding: '1rem', position: 'relative' }}>
      
      {/* OTP Modal Overlay */}
      {showOtpModal && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.8)', zIndex: 1000,
          display: 'flex', alignItems: 'flex-start', justifyContent: 'center',
          paddingTop: '10vh'
        }}>
          <div className="glass-panel animate-fade-in" style={{ padding: '2rem', maxWidth: '350px', width: '100%', textAlign: 'center', display: 'flex', flexDirection: 'column', alignItems: 'center', border: '1px solid #3b82f6' }}>
            <h2 style={{ fontSize: '1.5rem', marginBottom: '1rem', color: 'white' }}>Verify Your Email</h2>
            <p style={{ color: '#a1a1aa', marginBottom: '1.5rem', fontSize: '0.95rem' }}>
              We sent a 6-digit code to <strong>{email}</strong>. Enter it below to create your account.
            </p>
            <form onSubmit={handleVerifyOtp} style={{ width: '100%' }}>
              <input 
                type="text"
                maxLength="6"
                value={otpCode}
                onChange={(e) => setOtpCode(e.target.value)}
                placeholder="000000"
                style={{
                  width: '100%', padding: '1rem', background: 'rgba(255,255,255,0.1)',
                  border: '1px solid #3b82f6', borderRadius: '0.5rem', color: 'white',
                  textAlign: 'center', fontSize: '1.5rem', letterSpacing: '0.5rem',
                  outline: 'none', marginBottom: '1rem'
                }}
              />
              <button 
                type="submit" 
                className="btn-primary" 
                disabled={otpLoading}
                style={{ width: '100%', padding: '1rem', opacity: otpLoading ? 0.7 : 1 }}
              >
                {otpLoading ? 'Verifying...' : 'Verify OTP & Create Account'}
              </button>
              <button 
                type="button"
                onClick={() => setShowOtpModal(false)}
                style={{ background: 'transparent', border: 'none', color: '#ef4444', marginTop: '1rem', cursor: 'pointer' }}
              >
                Cancel
              </button>
            </form>
          </div>
        </div>
      )}


      {authStep === "method" && (
        <div className="animate-fade-in" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%', maxWidth: '350px' }}>
          
          <div style={{ 
            padding: '4px', borderRadius: '32px', 
            background: 'linear-gradient(135deg, rgba(59, 130, 246, 0.5), rgba(239, 68, 68, 0.2))',
            marginBottom: '2rem'
          }}>
            <img 
              src={muscleiqLogo} alt="MuscleIQ Logo" 
              style={{ width: '100px', height: '100px', borderRadius: '28px', objectFit: 'cover', display: 'block' }} 
            />
          </div>

          <h1 style={{ fontSize: '1.75rem', fontWeight: 'bold', color: 'white', marginBottom: '0.5rem', textAlign: 'center' }}>
            Welcome to MuscleIQ
          </h1>
          <p style={{ color: '#a1a1aa', fontSize: '0.95rem', marginBottom: '3rem', textAlign: 'center' }}>
            Log in or sign up to continue
          </p>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', width: '100%' }}>
            <button 
              onClick={handleGoogleSignIn}
              disabled={loading}
              style={{
                display: 'flex', alignItems: 'center', padding: '1rem 1.5rem',
                background: '#18181b', border: '1px solid #27272a', borderRadius: '1rem',
                color: 'white', cursor: loading ? 'not-allowed' : 'pointer', fontWeight: '600', fontSize: '1rem'
              }}
            >
              <div style={{ width: '24px', height: '24px', background: 'white', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', marginRight: '1rem' }}>
                <span style={{ color: 'black', fontWeight: 'bold', fontSize: '0.9rem' }}>G</span>
              </div>
              {loading ? 'Connecting...' : 'Continue with Google'}
            </button>

            <button 
              onClick={() => setAuthStep("login")}
              style={{
                display: 'flex', alignItems: 'center', padding: '1rem 1.5rem',
                background: '#18181b', border: '1px solid #27272a', borderRadius: '1rem',
                color: 'white', cursor: 'pointer', fontWeight: '600', fontSize: '1rem'
              }}
            >
              <Mail size={20} style={{ marginRight: '1rem' }} />
              Login with Email
            </button>

          </div>
        </div>
      )}

      {/* LOGIN OR REGISTER FORM */}
      {(authStep === "login" || authStep === "register") && (
        <div className="glass-panel animate-fade-in" style={{ padding: '2rem', maxWidth: '400px', width: '100%', textAlign: 'center', display: 'flex', flexDirection: 'column', alignItems: 'center', position: 'relative' }}>
          
          <button 
            onClick={() => { setAuthStep("method"); setError(""); setIsResetting(false); }}
            style={{ position: 'absolute', top: '1.5rem', left: '1.5rem', background: 'transparent', border: 'none', color: '#a1a1aa', cursor: 'pointer' }}
          >
            <ArrowLeft size={24} />
          </button>

          <img 
            src={muscleiqLogo} alt="MuscleIQ Logo" 
            style={{ width: '80px', height: '80px', borderRadius: '20px', border: '2px solid #8b5cf6', marginBottom: '1rem', marginTop: '1rem', objectFit: 'cover' }} 
          />
          <h1 style={{ fontSize: '1.5rem', marginBottom: '0.5rem' }}>MuscleIQ</h1>
          
          <p style={{ color: 'var(--text-muted)', marginBottom: '2rem' }}>
            {isResetting ? 'Reset your password' : authStep === "register" ? 'Create a new account' : 'Sign in with Email'}
          </p>
          
          <form onSubmit={isResetting ? handleResetPassword : (authStep === "register" ? handleSendOtp : handleLogin)} style={{ display: 'flex', flexDirection: 'column', gap: '1rem', width: '100%' }}>
            
            {authStep === "register" && !isResetting && (
              <div style={{ position: 'relative' }}>
                <User size={18} color="var(--text-muted)" style={{ position: 'absolute', top: '50%', transform: 'translateY(-50%)', left: '1rem' }} />
                <input 
                  type="text"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="Username"
                  required
                  style={{ 
                    width: '100%', padding: '1rem 1rem 1rem 3rem', background: 'rgba(0,0,0,0.2)', 
                    border: '1px solid var(--card-border)', borderRadius: '0.5rem', color: 'white', outline: 'none', boxSizing: 'border-box'
                  }}
                />
              </div>
            )}

            <div style={{ position: 'relative' }}>
              <Mail size={18} color="var(--text-muted)" style={{ position: 'absolute', top: '50%', transform: 'translateY(-50%)', left: '1rem' }} />
              <input 
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Email address"
                required
                style={{ 
                  width: '100%', padding: '1rem 1rem 1rem 3rem', background: 'rgba(0,0,0,0.2)', 
                  border: '1px solid var(--card-border)', borderRadius: '0.5rem', color: 'white', outline: 'none', boxSizing: 'border-box'
                }}
              />
            </div>
            
            {!isResetting && (
              <div style={{ position: 'relative' }}>
                <Lock size={18} color="var(--text-muted)" style={{ position: 'absolute', top: '50%', transform: 'translateY(-50%)', left: '1rem' }} />
                <input 
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Password"
                  required
                  minLength="6"
                  style={{ 
                    width: '100%', padding: '1rem 1rem 1rem 3rem', background: 'rgba(0,0,0,0.2)', 
                    border: '1px solid var(--card-border)', borderRadius: '0.5rem', color: 'white', outline: 'none', boxSizing: 'border-box'
                  }}
                />
              </div>
            )}

            <button 
              type="submit" 
              className="btn-primary" 
              disabled={loading}
              style={{ marginTop: '0.5rem', padding: '1rem', display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '0.5rem', opacity: loading ? 0.7 : 1 }}
            >
              {loading ? 'Processing...' : isResetting ? 'Send Reset Link' : authStep === "register" ? 'Continue (Send OTP)' : 'Sign In'}
            </button>
          </form>

          <div style={{ marginTop: '1.5rem', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
            {!isResetting && authStep === "login" && (
              <button 
                onClick={() => { setIsResetting(true); setError(""); }} 
                style={{ background: 'transparent', border: 'none', color: 'var(--text-muted)', cursor: 'pointer', fontSize: '0.9rem' }}
              >
                Forgot Password?
              </button>
            )}

            <button 
              onClick={() => { setAuthStep(authStep === "login" ? "register" : "login"); setIsResetting(false); setError(""); }} 
              style={{ background: 'transparent', border: 'none', color: '#3b82f6', cursor: 'pointer', textDecoration: 'underline' }}
            >
              {authStep === "register" ? 'Already have an account? Sign In' : 'Need an account? Register'}
            </button>
          </div>

          {error && (
            <div style={{ 
              marginTop: '1.5rem', padding: '1rem',
              backgroundColor: (error.includes("sent")) ? 'rgba(16, 185, 129, 0.2)' : 'rgba(239, 68, 68, 0.2)',
              border: `1px solid ${(error.includes("sent")) ? '#10b981' : '#ef4444'}`,
              borderRadius: '0.5rem',
              color: (error.includes("sent")) ? '#10b981' : '#ef4444', 
              fontSize: '0.95rem' 
            }}>
              {error}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
