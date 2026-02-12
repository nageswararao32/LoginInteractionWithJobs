import React, { useState } from 'react';
import Dashboard from './COMPONENTS/Dashboard';
import LoginPage from './COMPONENTS/Loginpage';


// Main App Component
export default function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);

    return (
        <div className="font-sans">
            {isLoggedIn ? (
                <Dashboard onLogout={() => setIsLoggedIn(false)} />
            ) : (
                <LoginPage onLogin={setIsLoggedIn} />
            )}
        </div>
    );
}
