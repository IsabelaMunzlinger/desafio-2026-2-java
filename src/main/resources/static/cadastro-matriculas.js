const token = localStorage.getItem('meuTokenJWT');

if (!token) {
    window.location.href = '/login';
}

const authHeaders = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
};

document.addEventListener('DOMContentLoaded', async () => {
    try {
        const resCursos = await fetch('/api/cursos', { headers: authHeaders });
        const selectCurso = document.getElementById('cursoId');
        if (resCursos.ok) {
            const cursos = await resCursos.json();
            selectCurso.innerHTML = '<option value="">Selecione um curso...</option>';
            cursos.forEach(curso => {
                selectCurso.innerHTML += `<option value="${curso.id}">${curso.nome}</option>`;
            });
        } else {
            selectCurso.innerHTML = '<option value="">Erro ao carregar cursos</option>';
        }

        const resUsuarios = await fetch('/api/usuarios/alunos', { headers: authHeaders });
        const selectAluno = document.getElementById('alunoId');
        if (resUsuarios.ok) {
            const usuarios = await resUsuarios.json();
            selectAluno.innerHTML = '<option value="">Selecione um aluno...</option>';
            
            usuarios.forEach(usuario => {
                selectAluno.innerHTML += `<option value="${usuario.id}">${usuario.nome} (${usuario.email})</option>`;
            });
        } else {
            selectAluno.innerHTML = '<option value="">Erro ao carregar alunos</option>';
        }

        // CARREGA A TABELA DE MATRÍCULAS AO ABRIR A TELA
        carregarMatriculasNaTabela();

    } catch (error) {
        console.error("Erro ao carregar dados:", error);
        alert("Erro de conexão com a API.");
    }
});

document.getElementById('formMatricula').addEventListener('submit', async (e) => {
    e.preventDefault();

    const matriculaDTO = {
        alunoId: document.getElementById('alunoId').value,
        cursoId: document.getElementById('cursoId').value
    };

    try {
        const response = await fetch('/api/matriculas', {
            method: 'POST',
            headers: authHeaders,
            body: JSON.stringify(matriculaDTO)
        });

        if (response.ok) {
            const mensagem = await response.text();
            alert(mensagem);
            document.getElementById('formMatricula').reset();

            // ATUALIZA A TABELA AUTOMATICAMENTE APÓS CADASTRAR UM NOVO
            carregarMatriculasNaTabela();
        } else {
            const erro = await response.text();
            alert("Atenção: " + erro);
        }
    } catch (error) {
        console.error("Erro ao matricular:", error);
        alert("Erro ao tentar realizar a matrícula.");
    }
});

// === FUNÇÕES DA TABELA (Adicionadas no final) ===

async function carregarMatriculasNaTabela() {
    const tbody = document.getElementById('tabelaMatriculasBody');
    if (!tbody) return; // Proteção caso o HTML da tabela não exista

    try {
        const response = await fetch('/api/matriculas', { headers: authHeaders });
        if (response.ok) {
            const matriculas = await response.json();
            tbody.innerHTML = '';

            if(matriculas.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4">Nenhuma matrícula encontrada.</td></tr>';
                return;
            }

            matriculas.forEach(mat => {
                const statusTexto = mat.ativo ? 'Ativo' : 'Inativo';
                const statusClasse = mat.ativo ? 'status-ativo' : 'status-inativo';
                const btnClasse = mat.ativo ? 'btn-inativar' : 'btn-ativar';
                const btnTexto = mat.ativo ? 'Inativar' : 'Ativar';

                tbody.innerHTML += `
                    <tr>
                        <td>${mat.aluno.nome}</td>
                        <td>${mat.curso.nome}</td>
                        <td class="${statusClasse}">${statusTexto}</td>
                        <td>
                            <button type="button" onclick="alternarStatus(${mat.id})" class="btn-acao ${btnClasse}">
                                ${btnTexto}
                            </button>
                        </td>
                    </tr>
                `;
            });
        }
    } catch (error) {
        console.error("Erro ao carregar tabela:", error);
        tbody.innerHTML = '<tr><td colspan="4">Erro ao carregar dados.</td></tr>';
    }
}

async function alternarStatus(idMatricula) {
    if (!confirm("Tem certeza que deseja alterar o status desta matrícula?")) return;

    try {
        const response = await fetch(`/api/matriculas/${idMatricula}/status`, {
            method: 'PATCH',
            headers: authHeaders
        });

        if (response.ok) {
            const mensagem = await response.text();
            alert(mensagem);
            carregarMatriculasNaTabela(); // Atualiza a tabela para trocar as cores do botão
        } else {
            alert("Erro ao alterar o status.");
        }
    } catch (error) {
        console.error("Erro:", error);
    }
}