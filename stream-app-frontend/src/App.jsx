import { useState, useRef } from 'react';
import VideoUpload from './components/VideoUpload';
import VideoPlayer from './components/VideoPlayer';
import { Button, TextInput } from 'flowbite-react';

function App() {
  const [videoId, setVideoId] = useState('ae9c3c18-c79d-4551-8ee0-ad08a9b00c7e');
  const [fieldValue, setFieldValue] = useState('');
  const videoPlayerRef = useRef(null);  // Reference to the video player section

  const handleStartWatching = () => {
    // Scroll to the video player section when "Start Watching" is clicked
    videoPlayerRef.current.scrollIntoView({ behavior: 'smooth' });
    setVideoId('ae9c3c18-c79d-4551-8ee0-ad08a9b00c7e');
  };

  return (
    <div className="min-h-screen flex flex-col bg-black text-white">
      {/* Intro Section */}
      <section className="relative bg-cover bg-center h-screen" style={{ backgroundImage: 'url(https://www.vdocipher.com/blog/wp-content/uploads/2024/05/video-streaming-sites.png)' }}>
        <div className="absolute inset-0 bg-black opacity-50"></div>
        <div className="container mx-auto text-center relative z-10 py-24">
          <h1 className="text-5xl font-bold mb-4">Welcome to Streamify</h1>
          <p className="text-xl mb-6">Stream your favorite movies and TV shows with ease</p>
          <Button
            onClick={handleStartWatching}  // Handle click to scroll to the video player
            className="bg-red-600 hover:bg-red-800 text-white px-6 py-3 rounded-full text-xl"
          >
            Start Watching
          </Button>
        </div>
      </section>

      {/* Header */}
      <header className="bg-transparent text-white py-6 fixed w-full z-10 top-0 left-0">
        <div className="container mx-auto flex justify-between items-center">
          <h1 className="text-3xl font-bold">Streamify</h1>
         
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-grow container mx-auto py-16 px-4 mt-24">
        <div className="grid md:grid-cols-2 gap-8">
          {/* Video Player Section */}
          <section ref={videoPlayerRef}>  {/* Reference to this section */}
            <h2 className="text-3xl font-semibold">Now Playing</h2>
            <div className="bg-black rounded-lg overflow-hidden shadow-lg">
              <VideoPlayer src={`http://localhost:1081/api/videos/${videoId}/master.m3u8`} />
            </div>
            <div className="mt-4 flex space-x-2">
              <TextInput
                value={fieldValue}
                onChange={(e) => setFieldValue(e.target.value)}
                placeholder="Enter video ID"
                className="flex-grow p-3 rounded-lg text-black"
              />
              <Button
                onClick={() => setVideoId(fieldValue)}
                className="bg-red-600 text-white hover:bg-red-800 p-3 rounded-lg"
              >
                Play
              </Button>
            </div>
          </section>

          {/* Video Upload Section */}
          <section className="flex flex-col space-y-4">
            <h2 className="text-3xl font-semibold">Upload Your Video</h2>
            <div className="bg-black rounded-lg overflow-hidden shadow-lg">
              <VideoUpload />
            </div>
          </section>
        </div>
      </main>

      {/* Footer */}
      <footer className="bg-black text-white py-6">
        <div className="container mx-auto text-center">
          <p>&copy; 2024 Streamify. All rights reserved.</p>
          <p>
            Built with ❤️ by <a href="https://github.com/Adityasingh814" className="text-red-600 hover:underline">Aditya Singh</a>
          </p>
        </div>
      </footer>
    </div>
  );
}

export default App;

