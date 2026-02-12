import React, { useState } from 'react';
import { Bookmark, X, MapPin, Briefcase, Clock, ArrowLeft } from 'lucide-react';

// Saved Jobs Modal
const SavedJobsModal = ({ savedJobs, onClose, onView, onRemove }) => {
    const [selectedJob, setSelectedJob] = useState(null);

    const handleViewJob = (job) => {
        setSelectedJob(job);
    };

    const handleBackToList = () => {
        setSelectedJob(null);
    };

    return (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-3xl w-full max-w-5xl max-h-[90vh] overflow-hidden shadow-2xl flex flex-col">
                <div className="bg-teal-600 text-white p-4 sm:p-6 flex justify-between items-center">
                    <div className="flex items-center gap-3">
                        {selectedJob && (
                            <button
                                onClick={handleBackToList}
                                className="hover:bg-teal-700 p-2 rounded-full transition-all"
                            >
                                <ArrowLeft size={24} />
                            </button>
                        )}
                        <Bookmark size={24} className="sm:block" />
                        <h2 className="text-xl sm:text-2xl font-bold">
                            {selectedJob ? selectedJob.title : `Saved Jobs (${savedJobs.length})`}
                        </h2>
                    </div>
                    <button onClick={onClose} className="hover:bg-teal-700 p-2 rounded-full transition-all">
                        <X size={24} />
                    </button>
                </div>

                <div className="p-4 sm:p-6 overflow-y-auto flex-1">
                    {selectedJob ? (
                        // Individual Job Detail View
                        <div className="bg-white rounded-2xl p-4 sm:p-8 shadow-lg border border-gray-100">
                            <div className="flex flex-col sm:flex-row justify-between items-start mb-6 gap-4">
                                <div className="flex-1">
                                    <h3 className="text-2xl sm:text-3xl font-bold text-teal-700 mb-3">{selectedJob.title}</h3>
                                    <p className="text-base sm:text-lg text-gray-600 mb-4">{selectedJob.company}</p>
                                </div>
                                <div className="bg-white border-2 border-gray-200 text-orange-500 w-16 h-16 sm:w-20 sm:h-20 rounded-full flex items-center justify-center font-bold text-3xl sm:text-4xl flex-shrink-0">
                                    V
                                </div>
                            </div>

                            {/* Job Details Grid */}
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-6">
                                <div className="flex items-center gap-3 bg-teal-50 p-4 rounded-xl">
                                    <MapPin className="text-teal-600 flex-shrink-0" size={24} />
                                    <div>
                                        <p className="text-xs text-gray-500 font-semibold">Location</p>
                                        <p className="text-sm sm:text-base font-semibold text-gray-800">{selectedJob.location}</p>
                                    </div>
                                </div>
                                <div className="flex items-center gap-3 bg-orange-50 p-4 rounded-xl">
                                    <Briefcase className="text-orange-600 flex-shrink-0" size={24} />
                                    <div>
                                        <p className="text-xs text-gray-500 font-semibold">Work Type</p>
                                        <p className="text-sm sm:text-base font-semibold text-gray-800">{selectedJob.type}</p>
                                    </div>
                                </div>
                                <div className="flex items-center gap-3 bg-purple-50 p-4 rounded-xl">
                                    <Clock className="text-purple-600 flex-shrink-0" size={24} />
                                    <div>
                                        <p className="text-xs text-gray-500 font-semibold">Duration</p>
                                        <p className="text-sm sm:text-base font-semibold text-gray-800">{selectedJob.duration}</p>
                                    </div>
                                </div>
                                <div className="flex items-center gap-3 bg-green-50 p-4 rounded-xl">
                                    <div className="text-green-600 font-bold text-2xl flex-shrink-0">₹</div>
                                    <div>
                                        <p className="text-xs text-gray-500 font-semibold">Salary</p>
                                        <p className="text-sm sm:text-base font-semibold text-gray-800">{selectedJob.salary}</p>
                                    </div>
                                </div>
                            </div>

                            {/* Description */}
                            <div className="mb-6">
                                <h4 className="text-lg sm:text-xl font-bold text-gray-800 mb-3">Job Description</h4>
                                <p className="text-sm sm:text-base text-gray-600 leading-relaxed bg-gray-50 p-4 rounded-xl">
                                    {selectedJob.description}
                                </p>
                            </div>

                            {/* Skills */}
                            <div className="mb-6">
                                <h4 className="text-lg sm:text-xl font-bold text-gray-800 mb-3">Required Skills</h4>
                                <div className="flex flex-wrap gap-2">
                                    {selectedJob.skills.map((skill, index) => (
                                        <span
                                            key={index}
                                            className="px-4 py-2 bg-teal-100 text-teal-700 rounded-full text-xs sm:text-sm font-semibold"
                                        >
                                            {skill}
                                        </span>
                                    ))}
                                </div>
                            </div>

                            {/* Posted Time */}
                            <div className="mb-6">
                                <p className="text-xs sm:text-sm text-gray-500">Posted {selectedJob.postedTime}</p>
                            </div>

                            {/* Action Buttons */}
                            <div className="flex flex-col sm:flex-row gap-3 pt-6 border-t border-gray-200">
                                <button
                                    onClick={() => onRemove(selectedJob.id)}
                                    className="flex-1 px-6 py-3 border-2 border-red-500 text-red-600 rounded-xl font-semibold hover:bg-red-50 transition-all"
                                >
                                    Remove from Saved
                                </button>
                                <button
                                    onClick={() => {
                                        onView(selectedJob);
                                        onClose();
                                    }}
                                    className="flex-1 px-6 py-3 bg-orange-500 text-white rounded-xl font-semibold hover:bg-orange-600 transition-all shadow-md"
                                >
                                    Apply Now
                                </button>
                            </div>
                        </div>
                    ) : (
                        // List of Saved Jobs
                        <>
                            {savedJobs.length === 0 ? (
                                <div className="text-center py-16">
                                    <Bookmark size={64} className="mx-auto text-gray-300 mb-4" />
                                    <p className="text-gray-500 text-lg font-semibold">No saved jobs yet</p>
                                    <p className="text-gray-400 text-sm mt-2">Save jobs you're interested in to view them here</p>
                                </div>
                            ) : (
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4 sm:gap-6">
                                    {savedJobs.map(job => (
                                        <div key={job.id} className="bg-white rounded-2xl p-4 sm:p-6 shadow-md border border-gray-100 relative hover:shadow-lg transition-all">
                                            <button
                                                onClick={() => onRemove(job.id)}
                                                className="absolute top-3 right-3 sm:top-4 sm:right-4 text-red-500 hover:bg-red-50 p-2 rounded-lg transition-all"
                                            >
                                                <X size={18} />
                                            </button>

                                            <div className="mb-4 pr-8">
                                                <h3 className="text-lg sm:text-xl font-bold text-teal-700 mb-2">{job.title}</h3>
                                                <p className="text-sm text-gray-600">{job.company}</p>
                                            </div>

                                            <div className="flex flex-wrap gap-2 mb-3 text-xs sm:text-sm text-gray-600">
                                                <span>• {job.location}</span>
                                                <span>• {job.type}</span>
                                            </div>

                                            <p className="text-xs sm:text-sm text-gray-600 mb-4 line-clamp-2">{job.description}</p>

                                            <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between pt-4 border-t border-gray-100 gap-3 sm:gap-0">
                                                <p className="text-base sm:text-lg font-bold text-teal-700">{job.salary}</p>
                                                <button
                                                    onClick={() => handleViewJob(job)}
                                                    className="w-full sm:w-auto px-4 sm:px-5 py-2 bg-orange-500 text-white rounded-lg font-semibold hover:bg-orange-600 transition-all text-sm sm:text-base"
                                                >
                                                    View Details
                                                </button>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </>
                    )}
                </div>
            </div>
        </div>
    );
};

export default SavedJobsModal;
