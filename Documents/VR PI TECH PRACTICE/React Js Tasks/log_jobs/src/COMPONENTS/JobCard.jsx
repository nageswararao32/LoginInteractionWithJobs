import React from 'react';

// Job Card Component
const JobCard = ({ job, onSave, onView, isSaved }) => {
    return (
        <div className="bg-white rounded-2xl p-4 sm:p-6 shadow-md hover:shadow-xl transition-all duration-300 border border-gray-100">
            <div className="flex justify-between items-start mb-4">
                <div className="flex-1">
                    <h3 className="text-lg sm:text-xl font-bold text-teal-700 mb-2">{job.title}</h3>
                    <p className="text-sm text-gray-600 mb-3">{job.company}</p>
                </div>
                <div className="bg-white border-2 border-gray-200 text-orange-500 w-12 h-12 sm:w-14 sm:h-14 rounded-full flex items-center justify-center font-bold text-xl sm:text-2xl flex-shrink-0 ml-2">
                    V
                </div>
            </div>

            <div className="flex flex-wrap items-center gap-2 mb-4 text-xs sm:text-sm text-gray-600">
                <span>• {job.location}</span>
                <span>• {job.type}</span>
                <span>• {job.duration}</span>
            </div>

            <p className="text-sm text-gray-600 mb-4 leading-relaxed">{job.description}</p>

            <div className="flex flex-wrap gap-2 mb-5">
                {job.skills.map((skill, index) => (
                    <span key={index} className="text-xs text-gray-600">
                        • {skill}
                    </span>
                ))}
            </div>

            <div className="flex flex-col sm:flex-row justify-between items-start sm:items-end pt-4 border-t border-gray-100 gap-2 sm:gap-0">
                <p className="text-base sm:text-lg font-semibold text-teal-700">
                    {job.salary}
                </p>

                <p className="text-xs text-gray-500">
                    {job.postedTime}
                </p>
            </div>

            <div className="flex flex-col sm:flex-row gap-3 mt-4">
                <button
                    onClick={() => onSave(job)}
                    className="flex-1 px-4 sm:px-5 py-3 border-2 border-orange-500 text-orange-600 rounded-xl font-semibold hover:bg-orange-50 transition-all text-sm sm:text-base"
                >
                    {isSaved ? 'Saved' : 'Save Job'}
                </button>
                <button
                    onClick={() => onView(job)}
                    className="flex-1 px-4 sm:px-5 py-3 bg-orange-500 text-white rounded-xl font-semibold hover:bg-orange-600 transition-all shadow-md text-sm sm:text-base"
                >
                    View Job
                </button>
            </div>
        </div>
    );
};

export default JobCard;
