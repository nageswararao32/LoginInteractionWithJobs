import React, { useState } from 'react';
import { X } from 'lucide-react';

// Post Job Modal
const PostJobModal = ({ onClose, onSubmit }) => {
    const [formData, setFormData] = useState({
        title: '',
        company: '',
        location: '',
        type: 'Remote',
        duration: 'Full-Time',
        salary: '',
        experience: '0-1',
        description: '',
        skills: ''
    });

    const handleSubmit = (e) => {
        e.preventDefault();
        const newJob = {
            ...formData,
            id: Date.now(),
            postedTime: 'Just now',
            skills: formData.skills.split(',').map(s => s.trim()),
            category: 'latest',
            isNew: true
        };
        onSubmit(newJob);
    };

    return (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-3xl w-full max-w-3xl max-h-[95vh] overflow-hidden shadow-2xl flex flex-col">
                <div className="bg-teal-600 text-white p-4 sm:p-6 flex justify-between items-center">
                    <h2 className="text-xl sm:text-2xl font-bold">Post a New Job</h2>
                    <button onClick={onClose} className="hover:bg-teal-700 p-2 rounded-full transition-all">
                        <X size={24} />
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="p-4 sm:p-8 overflow-y-auto flex-1">
                    <div className="space-y-6">
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div>
                                <label className="block text-sm font-semibold text-gray-700 mb-2">Job Title *</label>
                                <input
                                    type="text"
                                    required
                                    value={formData.title}
                                    onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                                    className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:border-teal-500 focus:outline-none focus:ring-2 focus:ring-teal-200"
                                    placeholder="e.g. UI UX Designer"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-gray-700 mb-2">Company Name *</label>
                                <input
                                    type="text"
                                    required
                                    value={formData.company}
                                    onChange={(e) => setFormData({ ...formData, company: e.target.value })}
                                    className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:border-teal-500 focus:outline-none focus:ring-2 focus:ring-teal-200"
                                    placeholder="e.g. VR PI Group"
                                />
                            </div>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div>
                                <label className="block text-sm font-semibold text-gray-700 mb-2">Location *</label>
                                <input
                                    type="text"
                                    required
                                    value={formData.location}
                                    onChange={(e) => setFormData({ ...formData, location: e.target.value })}
                                    className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:border-teal-500 focus:outline-none focus:ring-2 focus:ring-teal-200"
                                    placeholder="e.g. Hyderabad"
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-gray-700 mb-2">Salary *</label>
                                <input
                                    type="text"
                                    required
                                    value={formData.salary}
                                    onChange={(e) => setFormData({ ...formData, salary: e.target.value })}
                                    className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:border-teal-500 focus:outline-none focus:ring-2 focus:ring-teal-200"
                                    placeholder="e.g. ₹3-6 Lacs P.A."
                                />
                            </div>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div>
                                <label className="block text-sm font-semibold text-gray-700 mb-2">Work Type *</label>
                                <select
                                    value={formData.type}
                                    onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                                    className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:border-teal-500 focus:outline-none focus:ring-2 focus:ring-teal-200 bg-white"
                                >
                                    <option>Remote</option>
                                    <option>On-site</option>
                                    <option>Hybrid</option>
                                </select>
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-gray-700 mb-2">Duration *</label>
                                <select
                                    value={formData.duration}
                                    onChange={(e) => setFormData({ ...formData, duration: e.target.value })}
                                    className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:border-teal-500 focus:outline-none focus:ring-2 focus:ring-teal-200 bg-white"
                                >
                                    <option>Full-Time</option>
                                    <option>Part-Time</option>
                                    <option>Contract</option>
                                    <option>Internship</option>
                                </select>
                            </div>
                        </div>

                        <div>
                            <label className="block text-sm font-semibold text-gray-700 mb-2">Experience Required *</label>
                            <select
                                value={formData.experience}
                                onChange={(e) => setFormData({ ...formData, experience: e.target.value })}
                                className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:border-teal-500 focus:outline-none focus:ring-2 focus:ring-teal-200 bg-white"
                            >
                                <option value="0-1">Fresher (0-1 years)</option>
                                <option value="1-2">Entry Level (1-2 years)</option>
                                <option value="2-3">Junior (2-3 years)</option>
                                <option value="3-5">Mid Level (3-5 years)</option>
                                <option value="5-8">Senior (5-8 years)</option>
                                <option value="8-10">Lead (8-10 years)</option>
                                <option value="10-15">Principal (10-15 years)</option>
                                <option value="15+">Expert (15+ years)</option>
                            </select>
                        </div>

                        <div>
                            <label className="block text-sm font-semibold text-gray-700 mb-2">Description *</label>
                            <textarea
                                required
                                value={formData.description}
                                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                                className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:border-teal-500 focus:outline-none focus:ring-2 focus:ring-teal-200 resize-none"
                                rows="5"
                                placeholder="Job description..."
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-semibold text-gray-700 mb-2">Skills (comma separated) *</label>
                            <input
                                type="text"
                                required
                                value={formData.skills}
                                onChange={(e) => setFormData({ ...formData, skills: e.target.value })}
                                className="w-full px-4 py-3 rounded-xl border border-gray-300 focus:border-teal-500 focus:outline-none focus:ring-2 focus:ring-teal-200"
                                placeholder="e.g. UI/UX Design, Figma, Prototyping"
                            />
                        </div>
                    </div>

                    <div className="flex flex-col sm:flex-row gap-4 mt-8">
                        <button
                            type="button"
                            onClick={onClose}
                            className="flex-1 px-6 py-3 border-2 border-gray-300 text-gray-700 rounded-xl font-semibold hover:bg-gray-50 transition-all"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="flex-1 px-6 py-3 border-2 border-yellow-300 text-gray-700 rounded-xl font-semibold 
transition-all duration-300 hover:bg-orange-600 hover:shadow-lg hover:text-white hover:-translate-y-0.5 active:scale-95"
                        >
                            Post Job
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default PostJobModal;