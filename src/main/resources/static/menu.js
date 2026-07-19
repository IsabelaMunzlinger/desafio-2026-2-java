window.onload = function() {
    const token = localStorage.getItem('meuTokenJWT');

    if (!token) {
        alert('Acesso negado! Por favor, faça login primeiro.');
        window.location.href = '/login';
        return;
    }

    try {
        const payloadBase64Url = token.split('.')[1];
        const payloadDecodificado = JSON.parse(atob(payloadBase64Url));
        const permissao = String(payloadDecodificado.role || payloadDecodificado.perfil || payloadDecodificado.authorities || '').toUpperCase();

        if (permissao.includes('ADMIN')) {
                    document.getElementById('menuAdmin').style.display = 'block';
                    document.getElementById('boasVindas').innerText = "Painel do Administrador";
        }
        else if (permissao.includes('SECRETARIA')) {
            document.getElementById('menuSecretaria').style.display = 'block';
            document.getElementById('boasVindas').innerText = "Painel da Secretaria";
        }
        else if (permissao.includes('COORDENADOR')) {
            document.getElementById('menuCoordenador').style.display = 'block';
            document.getElementById('boasVindas').innerText = "Painel do Coordenador";
        }
        else {
            document.getElementById('menuAluno').style.display = 'block';
            document.getElementById('boasVindas').innerText = "Portal do Aluno";
        }
    } catch (erro) {
        console.error("Erro ao ler o token:", erro);
        sair();
    }
};

function sair() {
    localStorage.removeItem('meuTokenJWT');
    window.location.href = '/login';
}