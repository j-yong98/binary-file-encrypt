import axios from "axios";

const client = axios.create({
    baseURL: 'http://localhost:8080',
});

export async function sendFile(formdata: FormData) {
    await client.post('/api/file/upload', formdata)
        .then(() => console.log("파일 전송 성공!!!"))
        .catch((error) => console.error(error));
}

export async function loadFiles() {
    await client.get('/api/file').then((response) => {
        return response.data;
    }).catch((error) => {
        console.error(error);
    })
}

export async function downloadFile(filename: String) {
    await client.get(`/api/file/download/${filename}`)
        .then(response => {
            return response.data;
        }).catch(error => {
            console.error(error);
        })
}

export default client;