import axios from "axios";
import React from "react";


const client = axios.create({
    baseURL: 'http://localhost:8080',
});


export async function sendFile(
    formdata: FormData,
    headers: { 'SaveFilename': string, 'ChunkIndex': number, 'ChunkTotal': number }
) {
    return await client.post('/api/file/upload', formdata, {
        onUploadProgress: function (progressEvent: any) {
            let percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
        },
        headers: headers
    }).then((response) => response);
}

export async function loadFiles(page: number) {
    return await client.get('/api/file', {
        params: {
            page: page
        }
    }).then((response) => {
        return response.data
    });
}

export async function downloadFile(saveFilename: string, filename: string, start: number, end: number,
                                   setProgress: React.Dispatch<React.SetStateAction<number | undefined>>
                                   ) {
    return await client.get(`/api/file/download/${saveFilename}/${filename}`, {
        responseType: 'arraybuffer',
        onDownloadProgress: (event) => {
            console.log(event)
            if (event.total != undefined) {
                const per = Math.round((event.loaded * 100) / event.total);
                setProgress(per)
            }
        }
    });

}

export default client;