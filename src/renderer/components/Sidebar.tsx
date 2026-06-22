import React from 'react';
import '../styles/Sidebar.css';

interface SidebarProps {
  children: React.ReactNode;
}

const Sidebar: React.FC<SidebarProps> = ({ children }) => {
  return (
    <div className="sidebar">
      <div className="sidebar-content">
        {children}
      </div>
    </div>
  );
};

export default Sidebar;
