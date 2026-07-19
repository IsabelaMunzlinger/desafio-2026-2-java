window.onload = function() {
    const token = getToken();

    if (!token) {
        alert('Acesso negado! Faça login.');
        window.location.href = '/login';
        return;
    }

    try {
        const payloadBase64Url = token.split('.')[1];
        const payloadDecodificado = JSON.parse(atob(payloadBase64Url));
        const permissao = String(payloadDecodificado.role || payloadDecodificado.perfil || payloadDecodificado.authorities || '').toUpperCase();

        if (!permissao.includes('ADMIN')) {
            alert('Acesso negado! Área restrita para Administradores.');
            window.location.href = '/menu';
            return;
        }
    } catch (erro) {
        window.location.href = '/login';
        return;
    }

    const form = document.getElementById('formCurso');
    if (form) {
        form.addEventListener('submit', async function(e) {
            e.preventDefault();

            const nomeDoCurso = document.getElementById('nomeCurso').value;
            await salvarCurso(nomeDoCurso);
        });
    }
};


function getToken() {
    return localStorage.getItem('meuTokenJWT');
}

async function salvarCurso(nomeDoCurso) {
    try {
        const resposta = await fetch('/api/cursos', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${getToken()}` 
            },
            body: JSON.stringify({ nome: nomeDoCurso })
        });

        if (resposta.ok) {
            alert('Curso salvo com sucesso!');
            document.getElementById('nomeCurso').value = '';
        } else {
            alert('Erro ao salvar. Você tem permissão?');
        }
    } catch (erro) {
        console.error("Erro:", erro);
        alert("Falha na comunicação com o servidor.");
    }
}

async function editarCurso(idCurso, novoNome) {
    try {
        const resposta = await fetch(`/api/cursos/${idCurso}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${getToken()}`
            },
            body: JSON.stringify({ nome: novoNome })
        });

        if (resposta.ok) {
            alert('Curso atualizado!');
        }
    } catch (erro) {
        console.error("Erro:", erro);
    }
}

async function excluirCurso(idCurso) {
    if (!confirm('Tem certeza que deseja excluir este curso?')) return;

    try {
        const resposta = await fetch(`/api/cursos/${idCurso}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${getToken()}` 
            }
        });

        if (resposta.ok) {
            alert('Curso excluído com sucesso!');
        }
    } catch (erro) {
        console.error("Erro:", erro);
    }
}