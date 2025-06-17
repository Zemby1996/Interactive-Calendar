// src/components/AuthModal.js
import React, { useState, useContext } from 'react';
import { loginUser, registerUser } from '../api/apiService';
import AuthContext from '../context/AuthContext';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';

const AuthModal = ({ isOpen, onClose, initialMode = 'login' }) => {
    const [mode, setMode] = useState(initialMode); // 'login' or 'register'
    const [formData, setFormData] = useState({ email: '', password: '', firstName: '', lastName: '' });
    const [error, setError] = useState('');
    const { login } = useContext(AuthContext);
    const navigate = useNavigate();

    if (!isOpen) return null;

    const handleInputChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

     const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            if (mode === 'login') {
                const response = await loginUser({ email: formData.email, password: formData.password });
                login(response.data.data.token);
                onClose(); // Zamykamy modal
                navigate('/'); // 3. Przekierowujemy na stronę główną!
            } else {
                await registerUser(formData);
                alert('Rejestracja pomyślna! Możesz się teraz zalogować.'); // Można zamienić na toast
                setMode('login');
            }
        } catch (err) {
            const errorMessage = err.response?.data?.message || `Wystąpił błąd podczas ${mode === 'login' ? 'logowania' : 'rejestracji'}.`;
            setError(errorMessage);
        }
    };

    const switchMode = () => {
        setError('');
        setMode(prevMode => prevMode === 'login' ? 'register' : 'login');
    };

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <button onClick={onClose} className="modal-close-button">&times;</button>
                <h2>{mode === 'login' ? 'Logowanie' : 'Rejestracja'}</h2>
                <form onSubmit={handleSubmit}>
                    {mode === 'register' && (
                        <>
                            <div className="form-group"><label>Imię</label><input type="text" name="firstName" onChange={handleInputChange} required /></div>
                            <div className="form-group"><label>Nazwisko</label><input type="text" name="lastName" onChange={handleInputChange} required /></div>
                        </>
                    )}
                    <div className="form-group"><label>Email</label><input type="email" name="email" onChange={handleInputChange} required /></div>
                    <div className="form-group"><label>Hasło</label><input type="password" name="password" onChange={handleInputChange} required /></div>
                    {error && <p className="error-message">{error}</p>}
                    <button type="submit" className="modal-book-button">
                        {mode === 'login' ? 'Zaloguj' : 'Zarejestruj'}
                    </button>
                </form>
                <button onClick={switchMode} className="switch-mode-button">
                    {mode === 'login' ? 'Nie masz konta? Zarejestruj się' : 'Masz już konto? Zaloguj się'}
                </button>
            </div>
        </div>
    );
};

export default AuthModal;