
import React, { createContext, useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode'; 

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [auth, setAuth] = useState({ token: null, user: null });

    useEffect(() => {
        
        const token = localStorage.getItem('token');
        if (token) {
            const decodedUser = jwtDecode(token);
            setAuth({ token, user: decodedUser });
        }
    }, []);

    const login = (token) => {
        const decodedUser = jwtDecode(token);
        localStorage.setItem('token', token);
        setAuth({ token, user: decodedUser });
    };

    const logout = () => {
        localStorage.removeItem('token');
        setAuth({ token: null, user: null });
    };

    return (
        <AuthContext.Provider value={{ auth, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export default AuthContext;