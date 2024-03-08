import React, {ChangeEvent, FormEvent, useState} from 'react';
import {Button, Form} from 'react-bootstrap';
import {sendFile} from "../api";

const FileInput = () => {
    const [selectFile, setSelectedFile] = useState<File | null>(null);
    const submit = () => {
        if (!selectFile) {
            return;
        }
        const formdata = new FormData();
        formdata.append('file', selectFile);
        sendFile(formdata);
    };

    const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        setSelectedFile(file || null);
    };

    return (
        <div className="d-grid gap-2">
            <Form.Control
                id="target"
                onChange={handleFileChange}
                type="file"
            />
            <Button
                variant="primary"
                onClick={submit}
            >
                제출하기
            </Button>
        </div>
    );
};

export default FileInput;
