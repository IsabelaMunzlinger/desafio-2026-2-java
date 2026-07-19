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
            alert('Acesso negado!');
            window.location.href = '/menu';
            return;
        }
    } catch (erro) {
        window.location.href = '/login';
        return;
    }

    const form = document.getElementById('formDocumento');
    if (form) {
        form.addEventListener('submit', async function(e) {
            e.preventDefault();

            const dadosDocumento = {
                nome: document.getElementById('nome').value,
            };

            await salvarDocumento(dadosDocumento);
        });
    }
};

function getToken() {
    return localStorage.getItem('meuTokenJWT');
}

async function salvarDocumento(dados) {
    try {
        const resposta = await fetch('/api/documentos', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${getToken()}`
            },
            body: JSON.stringify(dados)
        });

        if (resposta.ok) {
            alert('Tipo de documento cadastrado com sucesso!');
            document.getElementById('formDocumento').reset();
        } else {
            alert('Erro ao cadastrar documento.');
        }
    } catch (erro) {
        console.error("Erro:", erro);
        alert("Falha na comunicação com o servidor.");
    }
}