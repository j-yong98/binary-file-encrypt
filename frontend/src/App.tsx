import './App.css';

import FileInput from "./components/FileInput";
import FileInfoView from "./components/FileInfoView";
import {useState} from "react";

function App() {
    const [selectFile, setSelectedFile] = useState<File | null>(null);
    const [progress, setProgress] = useState<number | undefined>(undefined);

    return (
        <div className="m-5">
            <FileInput selectFile={selectFile} setSelectedFile={setSelectedFile} progress={progress} setProgress={setProgress}/>
            <FileInfoView selectFile={selectFile} progress={progress} setProgress={setProgress}/>
        </div>
    );
}



export default App;
