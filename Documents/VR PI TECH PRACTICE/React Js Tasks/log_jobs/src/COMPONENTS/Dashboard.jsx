import React, { useState, useEffect } from 'react';
import { Search, User, Briefcase, LogOut, Bookmark } from 'lucide-react';
import JobCard from './JobCard';
import PostJobModal from './PostJobModal';
import SavedJobsModal from './SavedJobsModal';
import { initialJobs } from './initialJobs';

// Main Dashboard Component
const Dashboard = ({ onLogout }) => {
    const [jobs, setJobs] = useState(() => {
        const savedJobs = localStorage.getItem('jobs');
        return savedJobs ? JSON.parse(savedJobs) : initialJobs;
    });

    const [searchQuery, setSearchQuery] = useState('');
    const [showPostModal, setShowPostModal] = useState(false);
    const [showSavedModal, setShowSavedModal] = useState(false);
    const [showUserMenu, setShowUserMenu] = useState(false);
    const [savedJobs, setSavedJobs] = useState(() => {
        const saved = localStorage.getItem('savedJobs');
        return saved ? JSON.parse(saved) : [];
    });

    useEffect(() => {
        localStorage.setItem('jobs', JSON.stringify(jobs));
    }, [jobs]);

    useEffect(() => {
        localStorage.setItem('savedJobs', JSON.stringify(savedJobs));
    }, [savedJobs]);

    const handlePostJob = (newJob) => {
        setJobs([newJob, ...jobs]);
        setShowPostModal(false);
        alert('Job posted successfully!');
    };

    const handleSaveJob = (job) => {
        if (!savedJobs.find(j => j.id === job.id)) {
            setSavedJobs([...savedJobs, job]);
            alert('Job saved successfully!');
        } else {
            alert('Job already saved!');
        }
    };

    const handleRemoveSavedJob = (jobId) => {
        setSavedJobs(savedJobs.filter(j => j.id !== jobId));
    };

    const handleViewJob = (job) => {
        alert(`Viewing job: ${job.title} at ${job.company}\n\nDescription: ${job.description}\n\nSalary: ${job.salary}\nLocation: ${job.location}\nType: ${job.type} - ${job.duration}`);
    };

    const filteredJobs = jobs.filter(job => {
        const query = searchQuery.toLowerCase();
        return (
            job.title.toLowerCase().includes(query) ||
            job.company.toLowerCase().includes(query) ||
            job.location.toLowerCase().includes(query) ||
            job.skills.some(skill => skill.toLowerCase().includes(query))
        );
    });

    const recommendedJobs = filteredJobs.filter(j => j.category === 'recommended').slice(0, 10);
    const latestJobs = filteredJobs.filter(j => j.category === 'latest').slice(0, 20);

    return (
        <div className="min-h-screen bg-gradient-to-br from-teal-50 via-gray-50 to-orange-50">
            {/* Fixed Navbar */}
            <nav className="fixed top-0 left-0 right-0 bg-teal-700 shadow-lg z-40">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 py-3 sm:py-4">
                    <div className="flex items-center justify-between gap-2 sm:gap-3">
                        <div className="flex items-center gap-3 sm:gap-4">
                            <div className="text-white font-bold text-2xl sm:text-3xl font-sans">
                                LOGO
                            </div>

                            <div className="text-white -mt-2 text-base sm:text-lg hidden sm:block font-sans">
                                Jobs
                            </div>
                        </div>

                        {/* Search Bar */}
                        <div className="flex-1 max-w-xl">
                            <div className="relative">
                                <Search className="absolute left-3 sm:left-4 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
                                <input
                                    type="text"
                                    value={searchQuery}
                                    onChange={(e) => setSearchQuery(e.target.value)}
                                    placeholder="Search for something"
                                    className="w-full pl-10 sm:pl-12 pr-3 sm:pr-4 py-2 sm:py-3 text-sm sm:text-base rounded-full border-0 focus:outline-none focus:ring-2 focus:ring-white shadow-md"
                                />
                            </div>
                        </div>

                        {/* User Icons and Post Job */}
                        <div className="flex items-center gap-2 sm:gap-4">
                            <button className="bg-white/20 hover:bg-white/30 p-2 sm:p-3 rounded-full transition-all">
                                <Briefcase className="text-white" size={18} />
                            </button>
                            <div className="relative">
                                <button
                                    onClick={() => setShowUserMenu(!showUserMenu)}
                                    className="bg-orange-500 hover:bg-orange-600 p-2 sm:p-3 rounded-full transition-all shadow-md"
                                >
                                    <User className="text-white" size={18} />
                                </button>
                                {showUserMenu && (
                                    <div className="absolute right-0 mt-2 w-48 bg-white rounded-xl shadow-xl py-2 z-50">
                                        <button
                                            onClick={() => {
                                                onLogout();
                                                setShowUserMenu(false);
                                            }}
                                            className="w-full px-4 py-3 text-left hover:bg-gray-100 flex items-center gap-2 text-gray-700 font-semibold transition-all"
                                        >
                                            <LogOut size={18} />
                                            Logout
                                        </button>
                                    </div>
                                )}
                            </div>
                            <button
                                onClick={() => setShowPostModal(true)}
                                className="hidden sm:block bg-white text-teal-700 px-4 sm:px-6 py-2 rounded-full font-semibold hover:bg-gray-100 transition-all shadow-md whitespace-nowrap text-sm sm:text-base"
                            >
                                Post a Job?
                            </button>
                            <button
                                onClick={() => setShowPostModal(true)}
                                className="sm:hidden bg-white text-teal-700 px-3 py-2 rounded-full font-semibold hover:bg-gray-100 transition-all shadow-md text-xs"
                            >
                                Post Job
                            </button>
                        </div>
                    </div>
                </div>
            </nav>

            {/* Main Content */}
            <div className="pt-20 sm:pt-28 px-4 sm:px-6 pb-12 max-w-7xl mx-auto">
                {/* Recommended Jobs Section */}
                <div className="mb-12">
                    <div className="flex justify-between items-center mb-4 sm:mb-6">
                        <h2 className="text-2xl sm:text-3xl font-semibold text-teal-700">Recommended Jobs</h2>
                        <button className="text-sm sm:text-base text-teal-600 font-semibold hover:text-teal-700 transition-all">
                            View All
                        </button>
                    </div>
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-2 xl:grid-cols-3 gap-4 sm:gap-6">
                        {recommendedJobs.map(job => (
                            <JobCard
                                key={job.id}
                                job={job}
                                onSave={handleSaveJob}
                                onView={handleViewJob}
                                isSaved={savedJobs.some(j => j.id === job.id)}
                            />
                        ))}
                    </div>
                    {recommendedJobs.length === 0 && (
                        <div className="text-center py-12 text-gray-500">
                            No recommended jobs found matching your search.
                        </div>
                    )}
                </div>

                {/* Latest Jobs Section */}
                <div>
                    <div className="flex justify-between items-center mb-4 sm:mb-6">
                        <h2 className="text-2xl sm:text-3xl font-semibold text-teal-700">Latest Jobs</h2>
                        <button className="text-sm sm:text-base text-teal-600 font-semibold hover:text-teal-700 transition-all">
                            View All
                        </button>
                    </div>
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-2 xl:grid-cols-3 gap-4 sm:gap-6">
                        {latestJobs.map(job => (
                            <div key={job.id} className="relative">
                                {job.isNew && (
                                    <div className="absolute -top-2 -right-2 bg-green-500 text-white text-xs px-3 py-1 rounded-full font-bold shadow-lg z-10">
                                        NEW
                                    </div>
                                )}
                                <JobCard
                                    job={job}
                                    onSave={handleSaveJob}
                                    onView={handleViewJob}
                                    isSaved={savedJobs.some(j => j.id === job.id)}
                                />
                            </div>
                        ))}
                    </div>
                    {latestJobs.length === 0 && (
                        <div className="text-center py-12 text-gray-500">
                            No latest jobs found matching your search.
                        </div>
                    )}
                </div>
            </div>

            {/* Post Job Modal */}
            {showPostModal && (
                <PostJobModal
                    onClose={() => setShowPostModal(false)}
                    onSubmit={handlePostJob}
                />
            )}

            {/* Saved Jobs Modal */}
            {showSavedModal && (
                <SavedJobsModal
                    savedJobs={savedJobs}
                    onClose={() => setShowSavedModal(false)}
                    onView={handleViewJob}
                    onRemove={handleRemoveSavedJob}
                />
            )}

            {/* Floating Saved Jobs Counter */}
            {savedJobs.length > 0 && (
                <button
                    onClick={() => setShowSavedModal(true)}
                    className="fixed bottom-6 sm:bottom-8 right-4 sm:right-8 bg-orange-500 text-white px-4 sm:px-6 py-3 sm:py-4 rounded-full shadow-2xl flex items-center gap-2 hover:bg-orange-600 hover:scale-105 transition-all z-30"
                >
                    <Bookmark size={20} />
                    <span className="font-bold text-base sm:text-lg">{savedJobs.length} Saved</span>
                </button>
            )}
        </div>
    );
};

export default Dashboard;
