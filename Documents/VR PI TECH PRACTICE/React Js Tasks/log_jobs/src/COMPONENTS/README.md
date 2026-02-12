# Job Portal Application

This is a complete job portal application built with React. The application has been split into modular components for better maintainability and organization.

## File Structure

```
├── App.jsx                 # Main application component
├── LoginPage.jsx          # Login page component
├── Dashboard.jsx          # Main dashboard with job listings
├── JobCard.jsx            # Individual job card component
├── PostJobModal.jsx       # Modal for posting new jobs
├── SavedJobsModal.jsx     # Modal for viewing saved jobs
└── initialJobs.js         # Initial job data
```

## Features

### ✅ Complete Feature Set
- **User Authentication**: Login with credentials (admin@gmail.com / 123)
- **Job Listings**: Browse recommended and latest jobs
- **Search Functionality**: Search jobs by title, company, location, or skills
- **Save Jobs**: Save interesting jobs for later viewing
- **Post Jobs**: Create and post new job listings
- **View Job Details**: Click on saved jobs to see full details in card format
- **Mobile Responsive**: Fully optimized for mobile devices

### 📱 Mobile Optimization
- Responsive design that works on all screen sizes
- Touch-friendly buttons and interactions
- Optimized typography and spacing for mobile
- Collapsible navigation on smaller screens
- Mobile-friendly modals and overlays

### 💾 Saved Jobs Feature
- Save jobs by clicking "Save Job" button
- View all saved jobs in a dedicated modal
- Click "View Details" to see full job information in card format
- Remove jobs from saved list
- Persistent storage using localStorage

## How to Use

### Installation

1. Make sure all component files are in the same directory
2. Install dependencies:
```bash
npm install react lucide-react
```

### Running the Application

1. Import the main App component in your index.js or main entry file:
```javascript
import App from './App';
```

2. The application will start with the login page
3. Use these credentials to login:
   - Email: admin@gmail.com
   - Password: 123

### Component Import Structure

The components import each other in the following hierarchy:

```
App.jsx
├── LoginPage.jsx
└── Dashboard.jsx
    ├── JobCard.jsx
    ├── PostJobModal.jsx
    └── SavedJobsModal.jsx
        └── initialJobs.js
```

### Key Functionalities

#### Viewing Saved Jobs
1. Click on any job card's "Save Job" button
2. Click the floating "Saved" button (bottom-right corner)
3. In the saved jobs modal, click "View Details" on any job
4. The job will display in a detailed card format with:
   - Full job description
   - All skills listed
   - Location, work type, duration, and salary
   - Options to remove from saved or apply

#### Posting a Job
1. Click "Post a Job?" button in the navigation
2. Fill in all required fields
3. Click "Post Job" to add it to the listings
4. New jobs appear with a "NEW" badge

#### Searching Jobs
1. Use the search bar in the navigation
2. Search by job title, company, location, or skills
3. Results filter in real-time

## Technologies Used

- **React**: Frontend framework
- **Lucide React**: Icon library
- **Tailwind CSS**: Styling (utility classes)
- **LocalStorage**: Data persistence

## Credentials

- **Email**: admin@gmail.com
- **Password**: 123

## Notes

- All job data persists in localStorage
- Saved jobs persist across sessions
- The application is fully responsive
- No backend required - runs entirely in the browser

## Component Details

### App.jsx
Main component that manages authentication state and renders either LoginPage or Dashboard.

### LoginPage.jsx
Handles user authentication with email and password validation.

### Dashboard.jsx
Main application view containing:
- Navigation bar with search
- Recommended jobs section
- Latest jobs section
- Floating saved jobs counter

### JobCard.jsx
Reusable component for displaying individual job listings with save and view actions.

### PostJobModal.jsx
Modal form for creating and posting new job listings.

### SavedJobsModal.jsx
Modal for viewing saved jobs with two views:
- List view: Shows all saved jobs in a grid
- Detail view: Shows full job information in card format

### initialJobs.js
Contains the initial job data array with 30 pre-populated jobs.

## Customization

You can customize:
- Colors in the Tailwind classes
- Job data in initialJobs.js
- Form fields in PostJobModal.jsx
- Card layout in JobCard.jsx
- Authentication logic in LoginPage.jsx
