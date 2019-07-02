-------------------------------------------------------------------------------------
-- Banco


CREATE DATABASE "SciManager"
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'en_US.UTF-8'
       LC_CTYPE = 'en_US.UTF-8'
       CONNECTION LIMIT = -1;


-------------------------------------------------------------------------------------
-- Tabelas


-- GRUPO
CREATE TABLE grupo
(
  id_grupo bigint NOT NULL,
  nm_slug character varying(32),
  nm_nome_grupo character varying(255),
  CONSTRAINT grupo_pk PRIMARY KEY (id_grupo),
  CONSTRAINT grupo_nm_nome_grupo_key UNIQUE (nm_nome_grupo)
)
WITH (
  OIDS=FALSE
);


-- PROJETO_CIENTIFICO
CREATE TABLE projeto_cientifico
(
  id_projeto_cientifico bigint NOT NULL,
  nm_slug character varying(32),
  nm_nome_projeto character varying(255),
  CONSTRAINT projeto_cientifico_pk PRIMARY KEY (id_projeto_cientifico),
  CONSTRAINT projeto_cientifico_nm_nome_projeto_key UNIQUE (nm_nome_projeto)
)
WITH (
  OIDS=FALSE
);


-- EXPERIMENTO_CIENTIFICO
CREATE TABLE experimento_cientifico
(
  id_experimento_cientifico bigint NOT NULL,
  nm_slug character varying(32),
  nm_nome_experimento character varying(255),
  id_projeto_cientifico bigint NOT NULL,
  CONSTRAINT experimento_cientifico_pk PRIMARY KEY (id_experimento_cientifico),
  CONSTRAINT experimento_cientifico_nm_nome_experimento_key UNIQUE (nm_nome_experimento),
  CONSTRAINT experimento_cientifico_projeto_cientifico_fk FOREIGN KEY (id_projeto_cientifico)
      REFERENCES projeto_cientifico (id_projeto_cientifico) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);


-- WORKFLOW
CREATE TABLE workflow
(
  id_workflow bigint NOT NULL,
  nm_slug character varying(32),
  nm_versao character varying(255),
  nm_nome_workflow character varying(255),
  nm_swfms character varying(255),
  id_grupo bigint NOT NULL,
  id_projeto_cientifico bigint NOT NULL,
  id_experimento_cientifico bigint NOT NULL,
  CONSTRAINT workflow_pk PRIMARY KEY (id_workflow),
  CONSTRAINT workflow_nm_nome_workflow_key UNIQUE (nm_nome_workflow),
  CONSTRAINT workflow_projeto_cientifico_fk FOREIGN KEY (id_projeto_cientifico)
      REFERENCES projeto_cientifico (id_projeto_cientifico) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT workflow_experimento_cientifico_fk FOREIGN KEY (id_experimento_cientifico)
      REFERENCES experimento_cientifico (id_experimento_cientifico) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT workflow_grupo_fk FOREIGN KEY (id_grupo)
      REFERENCES grupo (id_grupo) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);


-- USUARIO
CREATE TABLE usuario
(
  id_usuario bigint NOT NULL,
  nm_slug character varying(32),
  nm_email character varying(255),
  nm_instituicao character varying(255),
  bo_tem_imagem_perfil boolean DEFAULT false,
  nm_nome character varying(255),
  nm_senha character varying(255),
  nm_perfil character varying,
  CONSTRAINT usuario_pk PRIMARY KEY (id_usuario),
  CONSTRAINT usuario_nm_email_key UNIQUE (nm_email)
)
WITH (
  OIDS=FALSE
);


-- IMAGEM_PERFIL
CREATE TABLE imagem_perfil
( 
  id_imagem_perfil bigint NOT NULL,
  nm_imagem_perfil_conteudo oid,
  id_usuario bigint NOT NULL,
  CONSTRAINT imagem_perfil_pk PRIMARY KEY (id_imagem_perfil),
  CONSTRAINT imagem_perfil_usuario_fk FOREIGN KEY (id_usuario)
      REFERENCES usuario (id_usuario) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);


-- ARQUIVO_MODELO
CREATE TABLE arquivo_modelo
(
  id_arquivo_modelo bigint NOT NULL,
  id_usuario bigint NOT NULL,
  bo_arquivo_atual boolean,
  dt_data_submissao timestamp without time zone,
  nm_conteudo_arquivo oid,
  nm_exectag character varying(255),
  id_workflow bigint NOT NULL,
  CONSTRAINT arquivo_modelo_pk PRIMARY KEY (id_arquivo_modelo),
  CONSTRAINT arquivo_modelo_usuario_fk FOREIGN KEY (id_usuario)
      REFERENCES usuario (id_usuario) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT arquivo_modelo_workflow_fk FOREIGN KEY (id_workflow)
      REFERENCES workflow (id_workflow) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);


-- FASE
CREATE TABLE fase
(
  id_fase bigint NOT NULL,
  nm_slug character varying(32),
  nm_nome_fase character varying(255),
  bo_habilita_execucao boolean NOT NULL DEFAULT false,
  id_projeto_cientifico bigint NOT NULL,
  CONSTRAINT fase_pk PRIMARY KEY (id_fase),
  CONSTRAINT fase_projeto_cientifico_fk FOREIGN KEY (id_projeto_cientifico)
      REFERENCES projeto_cientifico (id_projeto_cientifico) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);


-- TAG
CREATE TABLE tag
(
  id_tag bigint NOT NULL,
  nm_nome_tag character varying(255),
  CONSTRAINT tag_pk PRIMARY KEY (id_tag),
  CONSTRAINT tag_nm_nome_tag_key UNIQUE (nm_nome_tag)
)
WITH (
  OIDS=FALSE
);


-- EXECUCAO_WORKFLOW
CREATE TABLE execucao_workflow
(
  id_execucao_workflow bigint NOT NULL,
  nm_swfms character varying(255),
  nm_workflow_versao character varying(255),
  nm_exectag character varying(255),
  nm_status character varying,
  nm_log_execucao text,
  dt_data_execucao timestamp without time zone,
  id_usuario bigint NOT NULL,
  id_workflow bigint NOT NULL,
  id_arquivo_modelo bigint NOT NULL,
  CONSTRAINT execucao_workflow_pk PRIMARY KEY (id_execucao_workflow),
  CONSTRAINT execucao_workflow_usuario_fk FOREIGN KEY (id_usuario)
      REFERENCES usuario (id_usuario) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT execucao_workflow_workflow_fk FOREIGN KEY (id_workflow)
      REFERENCES workflow (id_workflow) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT execucao_workflow_arquivo_modelo_fk FOREIGN KEY (id_arquivo_modelo)
      REFERENCES arquivo_modelo (id_arquivo_modelo) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);


-- TAREFA
CREATE TABLE tarefa
(
  id_tarefa bigint NOT NULL,
  nm_descricao character varying(1000),
  dt_data_limite timestamp without time zone,
  nr_tempo_estimado integer,
  nm_status character varying,
  nm_titulo_tarefa character varying(255),
  nm_url_repositorio character varying(255),
  dt_data_criacao timestamp without time zone,
  id_workflow bigint NOT NULL,
  id_projeto_cientifico bigint NOT NULL,
  id_fase bigint NOT NULL,
  id_usuario bigint,
  id_grupo bigint,
  CONSTRAINT tarefa_pk PRIMARY KEY (id_tarefa),
  CONSTRAINT tarefa_grupo_fk FOREIGN KEY (id_grupo)
      REFERENCES grupo (id_grupo) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT tarefa_workflow_fk FOREIGN KEY (id_workflow)
      REFERENCES workflow (id_workflow) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT tarefa_projeto_cientifico_fk FOREIGN KEY (id_projeto_cientifico)
      REFERENCES projeto_cientifico (id_projeto_cientifico) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT tarefa_usuario_fk FOREIGN KEY (id_usuario)
      REFERENCES usuario (id_usuario) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT tarefa_fase_fk FOREIGN KEY (id_fase)
      REFERENCES fase (id_fase) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);


-- GRUPO_USUARIO
CREATE TABLE grupo_usuario
(
  id_usuario bigint NOT NULL,
  id_grupo bigint NOT NULL,
  CONSTRAINT grupo_usuario_pk PRIMARY KEY (id_usuario, id_grupo),
  CONSTRAINT grupo_usuario_fk FOREIGN KEY (id_grupo)
      REFERENCES grupo (id_grupo) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT usuario_grupo_fk FOREIGN KEY (id_usuario)
      REFERENCES usuario (id_usuario) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);


-- DOCUMENTACAO
CREATE TABLE documentacao
(
  id_documentacao bigint NOT NULL,
  nm_html_documentacao oid,
  id_workflow bigint,
  id_projeto_cientifico bigint,
  id_experimento_cientifico bigint,
  CONSTRAINT documentacao_pk PRIMARY KEY (id_documentacao),
  CONSTRAINT documentacao_workflow_fk FOREIGN KEY (id_workflow)
      REFERENCES workflow (id_workflow) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT documentacao_projeto_cientifico_fk FOREIGN KEY (id_projeto_cientifico)
      REFERENCES projeto_cientifico (id_projeto_cientifico) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT documentacao_experimento_cientifico_fk FOREIGN KEY (id_experimento_cientifico)
      REFERENCES experimento_cientifico (id_experimento_cientifico) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE documentacao
  OWNER TO postgres;


-- GRAFO_WORKFLOW
CREATE TABLE grafo_workflow
(
  id_grafo_workflow bigint NOT NULL,
  nm_codigo_grafo text,
  bo_grafo_detalhado boolean DEFAULT false,
  id_workflow bigint,
  CONSTRAINT grafo_workflow_pk PRIMARY KEY (id_grafo_workflow),
  CONSTRAINT grafo_workflow_workflow_fk FOREIGN KEY (id_workflow)
      REFERENCES workflow (id_workflow) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE grafo_workflow
  OWNER TO postgres;


-- TAG_TAREFA
CREATE TABLE tag_tarefa
(
  id_tag bigint NOT NULL,
  id_tarefa bigint NOT NULL,
  CONSTRAINT tag_tarefa_pk PRIMARY KEY (id_tag, id_tarefa),
  CONSTRAINT tag_tarefa_fk FOREIGN KEY (id_tag)
      REFERENCES tag (id_tag) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT tarefa_tag_fk FOREIGN KEY (id_tarefa)
      REFERENCES tarefa (id_tarefa) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

-- USUARIO_TAREFA
CREATE TABLE usuario_tarefa
(
  id_usuario bigint NOT NULL,
  id_tarefa bigint NOT NULL,
  CONSTRAINT usuario_tarefa_pk PRIMARY KEY (id_usuario, id_tarefa),
  CONSTRAINT usuario_tarefa_fk FOREIGN KEY (id_usuario)
      REFERENCES usuario (id_usuario) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT tarefa_usuario_fk FOREIGN KEY (id_tarefa)
      REFERENCES tarefa (id_tarefa) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);


-------------------------------------------------------------------------------------
-- Indices


CREATE INDEX idx_tarefa_workflow_projeto_cientifico
  ON tarefa
  USING btree
  (id_workflow, id_projeto_cientifico);


-------------------------------------------------------------------------------------
-- Sequences


-- ARQUIVO_MODELO
CREATE SEQUENCE arquivo_modelo_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE arquivo_modelo_id
  OWNER TO postgres;


-- PROJETO_CIENTIFICO
CREATE SEQUENCE projeto_cientifico_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE projeto_cientifico_id
  OWNER TO postgres;


-- FASE
CREATE SEQUENCE fase_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE fase_id
  OWNER TO postgres;


-- TAG
CREATE SEQUENCE tag_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE tag_id
  OWNER TO postgres;


-- TAREFA
CREATE SEQUENCE tarefa_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE tarefa_id
  OWNER TO postgres;


-- GRUPO
CREATE SEQUENCE grupo_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE grupo_id
  OWNER TO postgres;


-- USUARIO
CREATE SEQUENCE usuario_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE usuario_id
  OWNER TO postgres;

-- EXPERIMENTO_CIENTIFICO
CREATE SEQUENCE experimento_cientifico_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE experimento_cientifico_id
  OWNER TO postgres;


-- WORKFLOW
CREATE SEQUENCE workflow_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE workflow_id
  OWNER TO postgres;


-- EXECUCAO_WORKFLOW
CREATE SEQUENCE execucao_workflow_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE execucao_workflow_id
  OWNER TO postgres;


-- DOCUMENTACAO
CREATE SEQUENCE documentacao_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE documentacao_id
  OWNER TO postgres;


-- GRAFO_WORKFLOW
CREATE SEQUENCE grafo_workflow_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE grafo_workflow_id
  OWNER TO postgres;


-- IMAGEM_PERFIL
CREATE SEQUENCE imagem_perfil_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE imagem_perfil_id
  OWNER TO postgres;


