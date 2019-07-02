-- Dados default para criação de usuário administrador
-- email: scimanager.admin@uff.br
-- user: Admin
-- senha: sciadmin

INSERT INTO usuario 
(id_usuario, nm_email, nm_nome, nm_senha, nm_perfil, bo_tem_imagem_perfil)
VALUES 
(nextval('usuario_id'), 'scimanager.admin@uff.br', 'Admin', '$2a$10$3yJuPvcdEHWOLTJ0w7FXlO7/nP.8Rz4LH/yT4i3jYhPm3rj60chFy', 'ADMIN', false);
commit;

