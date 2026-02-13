import React, { useState, useEffect } from 'react';
import { Search, User, Briefcase, LogOut, Bookmark, MapPin, Clock } from 'lucide-react';
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
    const [locationFilter, setLocationFilter] = useState('');
    const [experienceFilter, setExperienceFilter] = useState('');
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

    // Helper function to check if experience matches filter
    const matchesExperience = (jobExperience, filterValue) => {
        if (!filterValue) return true;

        // Parse job experience range (e.g., "5-8" -> min: 5, max: 8)
        const expParts = jobExperience.split('-').map(e => parseInt(e.trim()));
        const jobMinExp = expParts[0] || 0;
        const jobMaxExp = expParts[1] || jobMinExp;

        switch (filterValue) {
            case 'fresher':
                // Fresher: 0-1 years
                return jobMinExp <= 1;
            case 'entry':
                // Entry Level: 0-2 years
                return jobMinExp <= 2;
            case 'mid':
                // Mid Level: 3-5 years
                return (jobMinExp >= 2 && jobMinExp <= 5) || (jobMaxExp >= 3 && jobMaxExp <= 6);
            case 'senior':
                // Senior: 5+ years
                return jobMinExp >= 5 || jobMaxExp >= 5;
            default:
                return true;
        }
    };

    // Filter jobs based on all criteria
    const filteredJobs = jobs.filter(job => {
        const query = searchQuery.toLowerCase();
        
        // Text search filter
        const matchesSearch = !searchQuery || (
            job.title.toLowerCase().includes(query) ||
            job.company.toLowerCase().includes(query) ||
            job.location.toLowerCase().includes(query) ||
            job.skills.some(skill => skill.toLowerCase().includes(query))
        );

        // Location filter
        const matchesLocation = !locationFilter || 
            job.location.toLowerCase().includes(locationFilter.toLowerCase());

        // Experience filter
        const matchesExp = matchesExperience(job.experience, experienceFilter);

        return matchesSearch && matchesLocation && matchesExp;
    });

    // Get unique locations from all jobs
    const uniqueLocations = [...new Set(jobs.map(job => job.location))].sort();

    const handleSearch = () => {
        // This function is called when "Search Job" button is clicked
        // The filtering already happens automatically through filteredJobs
        console.log('Searching with:', { searchQuery, locationFilter, experienceFilter });
    };

    const recommendedJobs = filteredJobs.filter(j => j.category === 'recommended').slice(0, 10);
    const latestJobs = filteredJobs.filter(j => j.category === 'latest').slice(0, 20);

    return (
        <div className="min-h-screen bg-gradient-to-br from-teal-50 via-gray-50 to-orange-50">
            {/* Fixed Navbar */}
            <nav className="fixed top-0 left-0 right-0 bg-teal-700 shadow-lg z-40">
                <div className="max-w-7xl mx-auto px-3 sm:px-6 py-3 sm:py-4">
                    {/* Top Row - Logo, Jobs, User Icons and Post Job */}
                    <div className="flex items-center justify-between gap-2 mb-3">
                        <div className="flex items-center gap-2 sm:gap-4">
                            <div className="text-white font-bold text-xl sm:text-3xl font-sans">
                                LOGO
                            </div>
                            <div className="text-white text-sm sm:text-lg font-sans">
                                Jobs
                            </div>
                        </div>

                        {/* User Icons and Post Job */}
                        <div className="flex items-center gap-2 sm:gap-3">
                            <button className="bg-white/20 hover:bg-white/30 p-2 rounded-full transition-all">
                                <Briefcase className="text-white" size={16} />
                            </button>
                            <div className="relative">
                                <button
                                    onClick={() => setShowUserMenu(!showUserMenu)}
                                    className="bg-orange-500 hover:bg-orange-600 p-2 rounded-full transition-all shadow-md"
                                >
                                    <User className="text-white" size={16} />
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
                                className="bg-white text-teal-700 px-3 sm:px-6 py-2 rounded-full font-semibold hover:bg-gray-100 transition-all shadow-md whitespace-nowrap text-xs sm:text-base"
                            >
                                Post a Job?
                            </button>
                        </div>
                    </div>

                    {/* Search and Filters Row - Single Row Layout */}
                    <div className="grid grid-cols-12 gap-2">
                        {/* Search Bar */}
                        <div className="col-span-12 sm:col-span-4">
                            <div className="relative">
                                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={16} />
                                <input
                                    type="text"
                                    value={searchQuery}
                                    onChange={(e) => setSearchQuery(e.target.value)}
                                    placeholder="Skills/ Job Role/ Company Name"
                                    className="w-full pl-9 pr-3 py-2 text-sm rounded-lg border-0 focus:outline-none focus:ring-2 focus:ring-white shadow-md"
                                />
                            </div>
                        </div>

                        {/* Location Filter */}
                        <div className="col-span-6 sm:col-span-3">
                            <div className="relative">
                                <MapPin className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 pointer-events-none z-10" size={16} />
                                <select
                                    value={locationFilter}
                                    onChange={(e) => setLocationFilter(e.target.value)}
                                    className="w-full pl-9 pr-2 py-2 text-sm rounded-lg border-0 focus:outline-none focus:ring-2 focus:ring-white shadow-md appearance-none bg-white cursor-pointer"
                                >
                                    <option value="">Location</option>
                                    {uniqueLocations.map((location, index) => (
                                        <option key={index} value={location}>
                                            {location}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>

                        {/* Experience Filter */}
                        <div className="col-span-6 sm:col-span-3">
                            <div className="relative">
                                <Clock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 pointer-events-none z-10" size={16} />
                                <select
                                    value={experienceFilter}
                                    onChange={(e) => setExperienceFilter(e.target.value)}
                                    className="w-full pl-9 pr-2 py-2 text-sm rounded-lg border-0 focus:outline-none focus:ring-2 focus:ring-white shadow-md appearance-none bg-white cursor-pointer"
                                >
                                    <option value="">Experience</option>
                                    <option value="fresher">Fresher (0-1 years)</option>
                                    <option value="entry">Entry Level (0-2 years)</option>
                                    <option value="mid">Mid Level (3-5 years)</option>
                                    <option value="senior">Senior (5+ years)</option>
                                </select>
                            </div>
                        </div>

                        {/* Search Button */}
                        <div className="col-span-12 sm:col-span-2">
                            <button
                                onClick={handleSearch}
                                className="w-full bg-white text-teal-700 px-4 py-2 rounded-lg font-semibold hover:bg-gray-100 transition-all shadow-md text-sm"
                            >
                                Search Job
                            </button>
                        </div>
                    </div>
                </div>
            </nav>

            {/* Main Content */}
            <div className="pt-32 sm:pt-36 px-3 sm:px-6 pb-12 max-w-7xl mx-auto">
                {/* Active Filters Display */}
                {(searchQuery || locationFilter || experienceFilter) && (
                    <div className="mb-6 flex flex-wrap items-center gap-2">
                        <span className="text-xs sm:text-sm text-gray-600 font-semibold">Active Filters:</span>
                        {searchQuery && (
                            <span className="bg-teal-100 text-teal-700 px-2 sm:px-3 py-1 rounded-full text-xs sm:text-sm flex items-center gap-1">
                                Search: {searchQuery}
                                <button onClick={() => setSearchQuery('')} className="ml-1 hover:text-teal-900 font-bold">×</button>
                            </span>
                        )}
                        {locationFilter && (
                            <span className="bg-teal-100 text-teal-700 px-2 sm:px-3 py-1 rounded-full text-xs sm:text-sm flex items-center gap-1">
                                Location: {locationFilter}
                                <button onClick={() => setLocationFilter('')} className="ml-1 hover:text-teal-900 font-bold">×</button>
                            </span>
                        )}
                        {experienceFilter && (
                            <span className="bg-teal-100 text-teal-700 px-2 sm:px-3 py-1 rounded-full text-xs sm:text-sm flex items-center gap-1">
                                Experience: {experienceFilter === 'fresher' ? 'Fresher (0-1 years)' : 
                                             experienceFilter === 'entry' ? 'Entry Level (0-2 years)' :
                                             experienceFilter === 'mid' ? 'Mid Level (3-5 years)' : 'Senior (5+ years)'}
                                <button onClick={() => setExperienceFilter('')} className="ml-1 hover:text-teal-900 font-bold">×</button>
                            </span>
                        )}
                        <button 
                            onClick={() => {
                                setSearchQuery('');
                                setLocationFilter('');
                                setExperienceFilter('');
                            }}
                            className="text-xs sm:text-sm text-teal-600 hover:text-teal-700 font-semibold underline"
                        >
                            Clear All
                        </button>
                    </div>
                )}

                {/* Recommended Jobs Section */}
                <div className="mb-12">
                    <div className="flex justify-between items-center mb-4 sm:mb-6">
                        <h2 className="text-xl sm:text-3xl font-semibold text-teal-700">
                            Recommended Jobs
                            {recommendedJobs.length > 0 && (
                                <span className="text-base sm:text-xl text-gray-500 ml-2">({recommendedJobs.length})</span>
                            )}
                        </h2>
                        <button className="text-xs sm:text-base text-teal-600 font-semibold hover:text-teal-700 transition-all">
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
                        <div className="text-center py-12 text-gray-500 text-sm sm:text-base">
                            No recommended jobs found matching your filters.
                        </div>
                    )}
                </div>

                {/* Latest Jobs Section */}
                <div>
                    <div className="flex justify-between items-center mb-4 sm:mb-6">
                        <h2 className="text-xl sm:text-3xl font-semibold text-teal-700">
                            Latest Jobs
                            {latestJobs.length > 0 && (
                                <span className="text-base sm:text-xl text-gray-500 ml-2">({latestJobs.length})</span>
                            )}
                        </h2>
                        <button className="text-xs sm:text-base text-teal-600 font-semibold hover:text-teal-700 transition-all">
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
                        <div className="text-center py-12 text-gray-500 text-sm sm:text-base">
                            No latest jobs found matching your filters.
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
                    <span className="font-bold text-sm sm:text-lg">{savedJobs.length} Saved</span>
                </button>
            )}
        </div>
    );
};

export default Dashboard;