import { GoogleGenerativeAI } from "@google/generative-ai";

const apiKey = import.meta.env.VITE_GEMINI_API_KEY || "YOUR_API_KEY";
const genAI = new GoogleGenerativeAI(apiKey);

export const generativeModel = genAI.getGenerativeModel({ model: "gemini-3.5-flash" });
