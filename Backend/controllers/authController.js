const nodemailer = require('nodemailer');
const { db } = require('../config/firebaseAdmin');
require('dotenv').config();

// Configure the email transporter using the provided credentials
const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: process.env.EMAIL_USER,
        pass: process.env.EMAIL_PASS
    }
});

// Helper to generate 6-digit OTP
const generateOtp = () => {
    return Math.floor(100000 + Math.random() * 900000).toString();
};

exports.sendOtp = async (req, res) => {
    const { email } = req.body;
    if (!email) {
        return res.status(400).json({ error: "Email is required" });
    }

    try {
        const otp = generateOtp();
        const expiresAt = Date.now() + 10 * 60 * 1000; // 10 minutes

        // Save OTP to Firestore
        await db.collection('OTPs').doc(email).set({
            code: otp,
            expiresAt: expiresAt
        });

        // Send Email
        const mailOptions = {
            from: process.env.EMAIL_USER,
            to: email,
            subject: 'Your MuscleIQ Verification Code',
            text: `Your MuscleIQ registration code is: ${otp}\n\nThis code will expire in 10 minutes.`
        };

        await transporter.sendMail(mailOptions);

        res.status(200).json({ message: "OTP sent successfully" });
    } catch (error) {
        console.error("Error sending OTP:", error);
        res.status(500).json({ error: "Failed to send OTP" });
    }
};

exports.verifyOtp = async (req, res) => {
    const { email, code } = req.body;
    if (!email || !code) {
        return res.status(400).json({ error: "Email and code are required" });
    }

    try {
        const otpDocRef = db.collection('OTPs').doc(email);
        const otpDoc = await otpDocRef.get();

        if (!otpDoc.exists) {
            return res.status(400).json({ error: "No OTP requested for this email" });
        }

        const otpData = otpDoc.data();

        if (otpData.code !== code) {
            return res.status(400).json({ error: "Invalid OTP code" });
        }

        if (Date.now() > otpData.expiresAt) {
            await otpDocRef.delete();
            return res.status(400).json({ error: "OTP has expired. Please request a new one." });
        }

        // OTP is valid
        // Delete it so it can't be reused
        await otpDocRef.delete();

        res.status(200).json({ message: "OTP verified successfully" });
    } catch (error) {
        console.error("Error verifying OTP:", error);
        res.status(500).json({ error: "Failed to verify OTP" });
    }
};
