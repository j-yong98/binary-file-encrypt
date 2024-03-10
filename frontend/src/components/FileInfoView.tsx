import React, {useEffect, useState} from 'react';
import {Button, Modal, Pagination, ProgressBar, Table} from 'react-bootstrap';
import {downloadFile, loadFiles} from "../api";
import {FaDownload} from 'react-icons/fa';

const FileInfoView = ({selectFile, progress, setProgress}: {
    selectFile: File | null,
    progress: number | undefined,
    setProgress: React.Dispatch<React.SetStateAction<number | undefined>>
}) => {

    const [data, setData] = useState([]);
    const [totalPages, setTotalPages] = useState<number>(0);
    const [currentPage, setCurrentPage] = useState<number>(0);
    const [isDone, setIsDone] = useState<boolean>(true);
    const [showModal, setShowModal] = useState<boolean>(false);
    const th = ['No.', '암호화 대상 파일', '암호화 된 파일', 'IV 값', '일시']

    useEffect(() => {
        fetchItems(currentPage);
    }, [selectFile])
    const fetchItems = (page: number) => {
        loadFiles(page).then(r => {
            setTotalPages(r.totalPages);
            setCurrentPage(page);
            setData(r.content);
        });
    }

    const handlePagination = (page: number) => {
        fetchItems(page);
    };

    const fileDownload = async (saveFilename: string, originalFilename: string) => {
        setIsDone(false);
        setShowModal(true);
        const chunkSize = 1024 * 1024;
        let start = 0;
        let end = chunkSize - 1;
        let chunks: BlobPart[] = [];

        while (!isDone) {
            try {
                await downloadFile(saveFilename, originalFilename, start, end).then((response) => {
                    console.log(response);
                    const status = response.status;
                    if (status === 200) {
                        const data = new Blob(chunks);
                        const fileURL = URL.createObjectURL(data);
                        setIsDone(true);

                        const a = document.createElement('a');
                        a.href = fileURL;
                        a.download = originalFilename;
                        document.body.appendChild(a);

                        a.click();

                        document.body.removeChild(a);
                        URL.revokeObjectURL(fileURL);
                    } else if (status === 206) {
                        chunks.push(response.data);
                        start = end + 1;
                        end = start + chunkSize - 1;
                    }
                });
            } catch (error) {
                console.error(error);
                break;
            }
        }
    }

    const closeModal = () => {
        setShowModal(false);
    }

    return (
        <div className="mt-5">
            <Table striped bordered hover>
                <thead>
                <tr className="text-center">
                    {
                        th.map((value: string) => {
                            return (
                                <th>{value}</th>
                            );
                        })
                    }
                </tr>
                </thead>
                <tbody>
                {
                    data.map((file: {
                        originalFile: {
                            originalFilename: string,
                            saveFilename: string
                        },
                        encryptFile: {
                            originalFilename: string,
                            saveFilename: string
                        },
                        ivValue: string,
                        uploadTime: string
                    }, idx: number) => {
                        return (
                            <tr className="text-center">
                                <td>{idx + 1}</td>
                                <td>
                                    <div className="d-flex justify-content-between align-items-center">
                                        {file.originalFile.originalFilename}
                                        <a href="#"
                                           onClick={() => fileDownload(file.originalFile.saveFilename, file.originalFile.originalFilename)}>
                                            <FaDownload/>
                                        </a>
                                    </div>
                                </td>
                                <td>
                                    <div className="d-flex justify-content-between align-items-center">
                                        {file.encryptFile.originalFilename}
                                        <a href="#"
                                           onClick={() => fileDownload(file.originalFile.saveFilename, file.originalFile.originalFilename)}>
                                            <FaDownload/>
                                        </a>
                                    </div>
                                </td>
                                <td>{file.ivValue}</td>
                                <td>{file.uploadTime}</td>
                            </tr>
                        );
                    })
                }
                </tbody>
            </Table>
            <div className="d-flex justify-content-end">
                <Pagination>
                    {Array.from(Array(totalPages).keys()).map((pageNumber) => (
                        <Pagination.Item key={pageNumber} active={pageNumber === currentPage}
                                         onClick={() => handlePagination(pageNumber)}>
                            {pageNumber + 1}
                        </Pagination.Item>
                    ))}
                </Pagination>
            </div>
            <Modal show={showModal} onHide={() => setShowModal(false)} centered>
                <Modal.Header>
                    <Modal.Title>파일 다운로드 진행 상태</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {
                        <ProgressBar now={progress} label={`${progress}%`}/>
                    }
                </Modal.Body>
                <Modal.Footer>
                    <Button onClick={closeModal}>닫기</Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default FileInfoView;
