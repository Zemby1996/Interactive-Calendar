// src/App.js
import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import HomePage from './pages/HomePage';
import Navbar from './components/Navbar';
import AuthModal from './components/AuthModal';
import MyBookingsPage from './pages/MyBookingsPage';
import AdminDashboardPage from './pages/AdminDashboardPage';
import ProtectedRoute from './components/ProtectedRoute';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';


function App() {
  const [isAuthModalOpen, setIsAuthModalOpen] = useState(false);
  const [authMode, setAuthMode] = useState('login'); // 'login' or 'register'

  const openModal = (mode) => {
    setAuthMode(mode);
    setIsAuthModalOpen(true);
  };

  const closeModal = () => {
    setIsAuthModalOpen(false);
  };

  return (
    <Router>
      <div className="App">
        <ToastContainer
          position="top-right"
          autoClose={5000}
          hideProgressBar={false}
          newestOnTop={false}
          closeOnClick
          rtl={false}
          pauseOnFocusLoss
          draggable
          pauseOnHover
          theme="colored"
        />
        <Navbar
          onLoginClick={() => openModal('login')}
          onRegisterClick={() => openModal('register')}
        />
        <main>
          {/* 2. Definiujemy, gdzie mają się renderować nasze strony */}
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/moje-wizyty" element={<MyBookingsPage />} />

            <Route element={<ProtectedRoute />}>
              <Route path="/panel-admina" element={<AdminDashboardPage />} />
            </Route>

          </Routes>
        </main>
        <AuthModal
          isOpen={isAuthModalOpen}
          onClose={closeModal}
          initialMode={authMode}
        />
      </div>
    </Router>
  );
}

export default App;