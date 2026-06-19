import React, { useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

const Navbar = () => {
  const { user, logout } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link to="/" className="navbar-logo-link">
          🏏 THANAL <span style={{ color: 'var(--accent-green)' }}>HEROES</span>
        </Link>
      </div>
      <div className="navbar-links">
        <Link to="/" className="navbar-link">Matches</Link>
        <Link to="/players" className="navbar-link">Players</Link>
        <Link to="/teams" className="navbar-link">Teams</Link>
        <Link to="/leaderboards" className="navbar-link">Leaderboard</Link>
      </div>
      <div className="navbar-auth">
        {user ? (
          <div className="navbar-user-info">
            <span className="navbar-username">{user.username}</span>
            <span className={`navbar-role-badge badge-${user.role.toLowerCase()}`}>
              {user.role}
            </span>
            <button className="btn btn-secondary btn-sm" onClick={handleLogout}>
              Logout
            </button>
          </div>
        ) : (
          <div className="navbar-auth-btns">
            <Link to="/login" className="btn btn-secondary btn-sm">Login</Link>
            <Link to="/register" className="btn btn-primary btn-sm">Register</Link>
          </div>
        )}
      </div>
    </nav>
  );
};

// Add active page indicator support via CSS inject
const styleSheet = document.createElement("style");
styleSheet.innerText = `
  nav a:hover {
    color: var(--accent-green) !important;
  }
`;
document.head.appendChild(styleSheet);

export default Navbar;
