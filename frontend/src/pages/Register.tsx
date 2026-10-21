import { useState } from 'react';
import React from 'react';
import { useNavigate, Link } from 'react-router-dom';

export default function Register() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState(''); 
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const doPasswordsMatch = password === confirmPassword;

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');

        if (!doPasswordsMatch) {
            setError("Passwords do not match!");
            return;
        }

        try {
            const response = await fetch("http://localhost:8080/api/auth/register", {
                method: 'POST',
                headers: { 'content-type': 'application/json' },
                body: JSON.stringify({ email, password })
            });

            if (!response.ok) {
                setError('This email address is already in use!');
                return;
            }

            alert("Registered successfully! Please log in.");
            navigate('/login');

        } catch (er) {
            console.error(er);
            setError("Something went wrong. Please try again.");
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <div className="bg-white p-8 rounded-lg shadow-lg w-full max-w-md">
                
                <h2 className="text-2xl font-bold mb-6 text-center text-gray-800">
                    Create an Account
                </h2>

                {error && (
                    <p className="text-red-500 text-sm mb-4 text-center bg-red-50 p-2 rounded">
                        {error}
                    </p>
                )}

                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label className="block text-gray-700 text-sm font-bold mb-2">Email</label>
                        <input 
                            type="email" 
                            onChange={(e) => setEmail(e.target.value)} 
                            className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            required
                        />
                    </div>

                    <div className="mb-4">
                        <label className="block text-gray-700 text-sm font-bold mb-2">Password</label>
                        <input 
                            type="password" 
                            onChange={(e) => setPassword(e.target.value)} 
                            className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            required
                        />
                    </div>

                    <div className="mb-6">
                        <label className="block text-gray-700 text-sm font-bold mb-2">Confirm Password</label>
                        <input 
                            type="password" 
                            onChange={(e) => setConfirmPassword(e.target.value)} 
                            className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            required
                        />
                        
                        {/* Show error ONLY if they typed something in confirm AND it doesn't match */}
                        {confirmPassword.length > 0 && !doPasswordsMatch && (
                            <p className="text-sm text-red-500 mt-1">Passwords do not match*</p>
                        )}
                    </div>

                    <button 
                        type="submit" 
                        className="w-full bg-green-600 text-white font-semibold py-2 rounded-md hover:bg-green-700 transition duration-200"
                    >
                        Register
                    </button>
                </form>

                <p className="mt-4 text-center text-sm text-gray-600">
                    Already have an account?{' '}
                    <Link to="/login" className="text-blue-600 hover:underline font-semibold">
                        Log in here
                    </Link>
                </p>

            </div>
        </div>
    );
}