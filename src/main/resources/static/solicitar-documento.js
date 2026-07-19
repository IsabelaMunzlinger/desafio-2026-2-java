const token = localStorage.getItem('meuTokenJWT');

if (!token) {
    window.location.href = '/login';
}

const authHeaders = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
};

// Carrega os documentos e os cursos disponíveis ao abrir a tela
document.addEventListener('DOMContentLoaded', async () => {
    try {
        // Busca e preenche os Documentos
        const resDocs = await fetch('/api/documentos', { headers: authHeaders });
        const selectDoc = document.getElementById('documentoId');

        if (resDocs.ok) {
            const documentos = await resDocs.json();
            selectDoc.innerHTML = '<option value="">Selecione um documento...</option>';
            documentos.forEach(doc => {
                selectDoc.innerHTML += `<option value="${doc.id}">${doc.nome}</option>`;
            });
        } else {
            selectDoc.innerHTML = '<option value="">Erro ao carregar documentos</option>';
        }

        // Busca e preenche os cursos ativos do aluno
        const resCursos = await fetch('/api/cursos/meus-cursos', { headers: authHeaders });
        const selectCurso = document.getElementById('cursoId');

        if (resCursos.ok) {
            const cursos = await resCursos.json();
            selectCurso.innerHTML = '<option value="">Selecione o seu curso...</option>';
            cursos.forEach(curso => {
                selectCurso.innerHTML += `<option value="${curso.id}">${curso.nome}</option>`;
            });
        } else {
            if(selectCurso) selectCurso.innerHTML = '<option value="">Erro ao carregar cursos</option>';
        }

    } catch (error) {
        console.error("Erro ao carregar dados:", error);
    }
});

//Envia a solicitação
document.getElementById('formPedido').addEventListener('submit', async (e) => {
    e.preventDefault();

    const solicitacaoDTO = {
        documentoId: document.getElementById('documentoId').value,
        cursoId: document.getElementById('cursoId').value
    };

    try {
        const response = await fetch('/api/solicitacoes', {
            method: 'POST',
            headers: authHeaders,
            body: JSON.stringify(solicitacaoDTO)
        });

        if (response.ok) {
            alert("Documento solicitado com sucesso!");
            document.getElementById('formPedido').reset();
        } else {
            const erro = await response.text();
            alert("Atenção: " + erro);
        }
    } catch (error) {
        console.error("Erro na requisição:", error);
        alert("Erro ao solicitar documento.");
    }
});
