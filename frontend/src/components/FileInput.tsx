import React, {ChangeEvent, useRef, useState} from 'react';
import {Button, Form} from 'react-bootstrap';
import {sendFile} from "../api";
import {ProgressBar, Modal} from "react-bootstrap";

const FileInput = ({selectFile, setSelectedFile, progress, setProgress}: {
    selectFile: File | null,
    setSelectedFile: React.Dispatch<React.SetStateAction<File | null>>,
    progress: number | undefined,
    setProgress: React.Dispatch<React.SetStateAction<number | undefined>>
}) => {
    const [showModal, setShowModal] = useState<boolean>(false);
    const inputFile = useRef<HTMLInputElement>(null);
    const closeModal = () => {
        setShowModal(false);
        setProgress(0);
    }
    const submit = () => {
        if (!selectFile) {
            alert('파일을 선택해 주세요.')
            return;
        }

        const chunks: BlobPart[] = [];
        const chunkSize = 1024 * 1024;
        const total = Math.ceil(selectFile.size / chunkSize);

        let size = 0;
        while (size < selectFile.size) {
            const curSize = Math.min(size + chunkSize, selectFile.size);
            const chunk = selectFile.slice(size, curSize);
            chunks.push(chunk)
            size = curSize
        }

        setShowModal(true);
        send(chunks, total);
    };

    const send = async (chunks: BlobPart[], total: number) => {
        if (selectFile === null) {
            return;
        }
        let saveFilename = ''
        for (let i = 0; i < chunks.length; i++) {
            let file = new File([chunks[i]], selectFile.name);
            const formdata = new FormData();
            formdata.append('file', file);
            try {
                await sendFile(formdata, {
                    'SaveFilename': saveFilename,
                    'ChunkIndex': i + 1,
                    'ChunkTotal': total
                }).then((response) => {
                    const {data} = response;
                    saveFilename = data.saveFilename;
                    const currentProgress = Math.round((i + 1) / total * 100);
                    setProgress(currentProgress);
                    if (data.status === 'OK' && inputFile && inputFile.current) {
                        inputFile.current.value = '';
                        setSelectedFile(null);
                    }
                }).catch(error => {
                    console.error(error);
                    alert("파일 업로드 실패!");
                    setProgress(undefined);
                });
            } catch (error) {
                console.error(error);
            }
        }
    }


    const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];

        if (file) {
            const ext = file.name.split('.').pop(); // 파일 확장자를 가져옵니다.

            if (ext !== 'bin') {
                alert('바이너리 파일이 아닙니다');
                event.target.value = '';
                return;
            }
            setSelectedFile(file || null);
        }
    };

    return (
        <div className="d-grid gap-2">
            <Form.Control
                ref={inputFile}
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
            <Modal
                backdrop='static'
                show={showModal} onHide={() => setShowModal(false)} centered>
                <Modal.Header>
                    <Modal.Title>파일 업로드 진행 상태</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {
                       <ProgressBar now={progress} label={`${progress}%`}/>
                    }
                </Modal.Body>
                <Modal.Footer>
                    <Button onClick={() => closeModal()}>닫기</Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default FileInput;
