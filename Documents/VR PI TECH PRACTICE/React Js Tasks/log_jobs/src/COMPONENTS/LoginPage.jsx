import React, { useState } from 'react';

// Login Page Component
const LoginPage = ({ onLogin }) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        if (email === 'admin@gmail.com' && password === '123') {
            onLogin(true);
            setError('');
        } else {
            setError('Invalid credentials. Use email: admin@gmail.com, password: 123');
        }
    };

    return (
        <div className="min-h-screen bg-gradient-to-br from-teal-50 via-white to-orange-50 flex items-center justify-center p-4">
            <div className="w-full max-w-md">
                <div className="bg-white rounded-3xl shadow-xl p-6 sm:p-10 border border-gray-100">
                    <div className="text-center mb-8">
                        <div className="inline-block shadow-md" style={{ borderRadius: "60%" }}>
                            <img
                                src="data:image/jpeg;base64,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"
                                alt="Company Logo"
                                className="w-24 sm:w-28 h-auto object-cover rounded-full"
                            />
                        </div>
                        <h1 className="text-2xl sm:text-3xl font-bold text-gray-800 mb-2 mt-4">Welcome Back</h1>
                        <p className="text-sm sm:text-base text-gray-600">Sign in to access the job portal</p>
                    </div>

                    <form onSubmit={handleSubmit} className="space-y-5">
                        <div>
                            <label className="block text-sm font-semibold text-gray-700 mb-2">Email</label>
                            <input
                                type="text"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:border-teal-500 focus:outline-none focus:ring-2 focus:ring-teal-200 transition-all"
                                placeholder="Enter your email"
                                required
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-semibold text-gray-700 mb-2">Password</label>
                            <input
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:border-teal-500 focus:outline-none focus:ring-2 focus:ring-teal-200 transition-all"
                                placeholder="Enter your password"
                                required
                            />
                        </div>

                        {error && (
                            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl text-sm">
                                {error}
                            </div>
                        )}

                        <button
                            type="submit"
                            className="w-full border-2 border-black text-teal-700 font-bold text-black py-3 rounded-xl hover:bg-teal-700 hover:text-white transition-all shadow-md hover:shadow-lg"
                        >
                            Sign In
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;
