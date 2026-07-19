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
            alert('Acesso negado! Somente administradores podem cadastrar usuários.');
            window.location.href = '/menu';
            return;
        }
    } catch (erro) {
        window.location.href = '/login';
        return;
    }

    const form = document.getElementById('formUsuario');
    if (form) {
        form.addEventListener('submit', async function(e) {
            e.preventDefault();

            const dadosUsuario = {
                nome: document.getElementById('nome').value,
                email: document.getElementById('email').value,
                senha: document.getElementById('senha').value,
                perfil: document.getElementById('perfil').value
            };

            await salvarUsuario(dadosUsuario);
        });
    }
};

function getToken() {
    return localStorage.getItem('meuTokenJWT');
}

async function salvarUsuario(dados) {
    try {
        const resposta = await fetch('/api/usuarios', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${getToken()}`
            },
            body: JSON.stringify(dados)
        });

        if (resposta.ok) {
            alert('Usuário cadastrado com sucesso!');
            document.getElementById('formUsuario').reset();
        } else {
            const erroMsg = await resposta.text();
            alert(`Erro ao cadastrar usuário: ${erroMsg || 'Verifique os dados.'}`);
        }
    } catch (erro) {
        console.error("Erro na requisição:", erro);
        alert("Falha na comunicação com o servidor.");
    }
}