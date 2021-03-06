PGDMP                 
        v        
   SciManager    9.5.14    9.5.14 z    	           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                       false            	           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                       false            	           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                       false             	           1262    16652 
   SciManager    DATABASE     ~   CREATE DATABASE "SciManager" WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8';
    DROP DATABASE "SciManager";
             postgres    false                        2615    2200    public    SCHEMA        CREATE SCHEMA public;
    DROP SCHEMA public;
             postgres    false            !	           0    0    SCHEMA public    COMMENT     6   COMMENT ON SCHEMA public IS 'standard public schema';
                  postgres    false    6            "	           0    0    SCHEMA public    ACL     �   REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;
                  postgres    false    6                        3079    12395    plpgsql 	   EXTENSION     ?   CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;
    DROP EXTENSION plpgsql;
                  false            #	           0    0    EXTENSION plpgsql    COMMENT     @   COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';
                       false    1            �            1259    16725    arquivo_modelo    TABLE     $  CREATE TABLE public.arquivo_modelo (
    id_arquivo_modelo bigint NOT NULL,
    id_usuario bigint NOT NULL,
    bo_arquivo_atual boolean,
    dt_data_submissao timestamp without time zone,
    nm_conteudo_arquivo oid,
    nm_exectag character varying(255),
    id_workflow bigint NOT NULL
);
 "   DROP TABLE public.arquivo_modelo;
       public         postgres    false    6            �            1259    16894    arquivo_modelo_id    SEQUENCE     z   CREATE SEQUENCE public.arquivo_modelo_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 (   DROP SEQUENCE public.arquivo_modelo_id;
       public       postgres    false    6            �            1259    16829    documentacao    TABLE     �   CREATE TABLE public.documentacao (
    id_documentacao bigint NOT NULL,
    nm_html_documentacao oid,
    id_workflow bigint,
    id_projeto_cientifico bigint,
    id_experimento_cientifico bigint
);
     DROP TABLE public.documentacao;
       public         postgres    false    6            �            1259    16914    documentacao_id    SEQUENCE     x   CREATE SEQUENCE public.documentacao_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 &   DROP SEQUENCE public.documentacao_id;
       public       postgres    false    6            �            1259    16758    execucao_workflow    TABLE     �  CREATE TABLE public.execucao_workflow (
    id_execucao_workflow bigint NOT NULL,
    nm_swfms character varying(255),
    nm_workflow_versao character varying(255),
    nm_exectag character varying(255),
    nm_status character varying,
    nm_log_execucao text,
    dt_data_execucao timestamp without time zone,
    id_usuario bigint NOT NULL,
    id_workflow bigint NOT NULL,
    id_arquivo_modelo bigint NOT NULL
);
 %   DROP TABLE public.execucao_workflow;
       public         postgres    false    6            �            1259    16912    execucao_workflow_id    SEQUENCE     }   CREATE SEQUENCE public.execucao_workflow_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 +   DROP SEQUENCE public.execucao_workflow_id;
       public       postgres    false    6            �            1259    16667    experimento_cientifico    TABLE     �   CREATE TABLE public.experimento_cientifico (
    id_experimento_cientifico bigint NOT NULL,
    nm_nome_experimento character varying(255),
    id_projeto_cientifico bigint NOT NULL
);
 *   DROP TABLE public.experimento_cientifico;
       public         postgres    false    6            �            1259    16908    experimento_cientifico_id    SEQUENCE     �   CREATE SEQUENCE public.experimento_cientifico_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 0   DROP SEQUENCE public.experimento_cientifico_id;
       public       postgres    false    6            �            1259    16740    fase    TABLE     �   CREATE TABLE public.fase (
    id_fase bigint NOT NULL,
    nm_nome_fase character varying(255),
    bo_habilita_execucao boolean DEFAULT false NOT NULL,
    id_projeto_cientifico bigint NOT NULL
);
    DROP TABLE public.fase;
       public         postgres    false    6            �            1259    16898    fase_id    SEQUENCE     p   CREATE SEQUENCE public.fase_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    DROP SEQUENCE public.fase_id;
       public       postgres    false    6            �            1259    16849    grafo_workflow    TABLE     �   CREATE TABLE public.grafo_workflow (
    id_grafo_workflow bigint NOT NULL,
    nm_codigo_grafo text,
    bo_grafo_detalhado boolean DEFAULT false,
    id_workflow bigint
);
 "   DROP TABLE public.grafo_workflow;
       public         postgres    false    6            �            1259    16916    grafo_workflow_id    SEQUENCE     z   CREATE SEQUENCE public.grafo_workflow_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 (   DROP SEQUENCE public.grafo_workflow_id;
       public       postgres    false    6            �            1259    16653    grupo    TABLE     f   CREATE TABLE public.grupo (
    id_grupo bigint NOT NULL,
    nm_nome_grupo character varying(255)
);
    DROP TABLE public.grupo;
       public         postgres    false    6            �            1259    16904    grupo_id    SEQUENCE     q   CREATE SEQUENCE public.grupo_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    DROP SEQUENCE public.grupo_id;
       public       postgres    false    6            �            1259    16814    grupo_usuario    TABLE     d   CREATE TABLE public.grupo_usuario (
    id_usuario bigint NOT NULL,
    id_grupo bigint NOT NULL
);
 !   DROP TABLE public.grupo_usuario;
       public         postgres    false    6            �            1259    16715    imagem_perfil    TABLE     �   CREATE TABLE public.imagem_perfil (
    id_imagem_perfil bigint NOT NULL,
    nm_imagem_perfil_conteudo oid,
    id_usuario bigint NOT NULL
);
 !   DROP TABLE public.imagem_perfil;
       public         postgres    false    6            �            1259    16918    imagem_perfil_id    SEQUENCE     y   CREATE SEQUENCE public.imagem_perfil_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 '   DROP SEQUENCE public.imagem_perfil_id;
       public       postgres    false    6            �            1259    16660    projeto_cientifico    TABLE     �   CREATE TABLE public.projeto_cientifico (
    id_projeto_cientifico bigint NOT NULL,
    nm_nome_projeto character varying(255)
);
 &   DROP TABLE public.projeto_cientifico;
       public         postgres    false    6            �            1259    16896    projeto_cientifico_id    SEQUENCE     ~   CREATE SEQUENCE public.projeto_cientifico_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 ,   DROP SEQUENCE public.projeto_cientifico_id;
       public       postgres    false    6            �            1259    16751    tag    TABLE     `   CREATE TABLE public.tag (
    id_tag bigint NOT NULL,
    nm_nome_tag character varying(255)
);
    DROP TABLE public.tag;
       public         postgres    false    6            �            1259    16900    tag_id    SEQUENCE     o   CREATE SEQUENCE public.tag_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
    DROP SEQUENCE public.tag_id;
       public       postgres    false    6            �            1259    16863 
   tag_tarefa    TABLE     ^   CREATE TABLE public.tag_tarefa (
    id_tag bigint NOT NULL,
    id_tarefa bigint NOT NULL
);
    DROP TABLE public.tag_tarefa;
       public         postgres    false    6            �            1259    16781    tarefa    TABLE     �  CREATE TABLE public.tarefa (
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
    id_grupo bigint
);
    DROP TABLE public.tarefa;
       public         postgres    false    6            �            1259    16902 	   tarefa_id    SEQUENCE     r   CREATE SEQUENCE public.tarefa_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
     DROP SEQUENCE public.tarefa_id;
       public       postgres    false    6            �            1259    16704    usuario    TABLE     *  CREATE TABLE public.usuario (
    id_usuario bigint NOT NULL,
    nm_email character varying(255),
    nm_instituicao character varying(255),
    bo_tem_imagem_perfil boolean DEFAULT false,
    nm_nome character varying(255),
    nm_senha character varying(255),
    nm_perfil character varying
);
    DROP TABLE public.usuario;
       public         postgres    false    6            �            1259    16906 
   usuario_id    SEQUENCE     s   CREATE SEQUENCE public.usuario_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 !   DROP SEQUENCE public.usuario_id;
       public       postgres    false    6            �            1259    16878    usuario_tarefa    TABLE     f   CREATE TABLE public.usuario_tarefa (
    id_usuario bigint NOT NULL,
    id_tarefa bigint NOT NULL
);
 "   DROP TABLE public.usuario_tarefa;
       public         postgres    false    6            �            1259    16679    workflow    TABLE     2  CREATE TABLE public.workflow (
    id_workflow bigint NOT NULL,
    nm_versao character varying(255),
    nm_nome_workflow character varying(255),
    nm_swfms character varying(255),
    id_grupo bigint NOT NULL,
    id_projeto_cientifico bigint NOT NULL,
    id_experimento_cientifico bigint NOT NULL
);
    DROP TABLE public.workflow;
       public         postgres    false    6            �            1259    16910    workflow_id    SEQUENCE     t   CREATE SEQUENCE public.workflow_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 "   DROP SEQUENCE public.workflow_id;
       public       postgres    false    6            	           2613    18815    18815    BLOB     &   SELECT pg_catalog.lo_create('18815');
 &   SELECT pg_catalog.lo_unlink('18815');
             postgres    false            	           2613    18816    18816    BLOB     &   SELECT pg_catalog.lo_create('18816');
 &   SELECT pg_catalog.lo_unlink('18816');
             postgres    false            	           2613    18817    18817    BLOB     &   SELECT pg_catalog.lo_create('18817');
 &   SELECT pg_catalog.lo_unlink('18817');
             postgres    false            	           2613    19309    19309    BLOB     &   SELECT pg_catalog.lo_create('19309');
 &   SELECT pg_catalog.lo_unlink('19309');
             postgres    false            	           2613    19310    19310    BLOB     &   SELECT pg_catalog.lo_create('19310');
 &   SELECT pg_catalog.lo_unlink('19310');
             postgres    false            �          0    16725    arquivo_modelo 
   TABLE DATA               �   COPY public.arquivo_modelo (id_arquivo_modelo, id_usuario, bo_arquivo_atual, dt_data_submissao, nm_conteudo_arquivo, nm_exectag, id_workflow) FROM stdin;
    public       postgres    false    187   ��       $	           0    0    arquivo_modelo_id    SEQUENCE SET     ?   SELECT pg_catalog.setval('public.arquivo_modelo_id', 3, true);
            public       postgres    false    197            	          0    16829    documentacao 
   TABLE DATA               �   COPY public.documentacao (id_documentacao, nm_html_documentacao, id_workflow, id_projeto_cientifico, id_experimento_cientifico) FROM stdin;
    public       postgres    false    193   6�       %	           0    0    documentacao_id    SEQUENCE SET     >   SELECT pg_catalog.setval('public.documentacao_id', 1, false);
            public       postgres    false    207            	          0    16758    execucao_workflow 
   TABLE DATA               �   COPY public.execucao_workflow (id_execucao_workflow, nm_swfms, nm_workflow_versao, nm_exectag, nm_status, nm_log_execucao, dt_data_execucao, id_usuario, id_workflow, id_arquivo_modelo) FROM stdin;
    public       postgres    false    190   S�       &	           0    0    execucao_workflow_id    SEQUENCE SET     C   SELECT pg_catalog.setval('public.execucao_workflow_id', 1, false);
            public       postgres    false    206            �          0    16667    experimento_cientifico 
   TABLE DATA               w   COPY public.experimento_cientifico (id_experimento_cientifico, nm_nome_experimento, id_projeto_cientifico) FROM stdin;
    public       postgres    false    183   ��       '	           0    0    experimento_cientifico_id    SEQUENCE SET     G   SELECT pg_catalog.setval('public.experimento_cientifico_id', 1, true);
            public       postgres    false    204            �          0    16740    fase 
   TABLE DATA               b   COPY public.fase (id_fase, nm_nome_fase, bo_habilita_execucao, id_projeto_cientifico) FROM stdin;
    public       postgres    false    188   �       (	           0    0    fase_id    SEQUENCE SET     5   SELECT pg_catalog.setval('public.fase_id', 3, true);
            public       postgres    false    199            	          0    16849    grafo_workflow 
   TABLE DATA               m   COPY public.grafo_workflow (id_grafo_workflow, nm_codigo_grafo, bo_grafo_detalhado, id_workflow) FROM stdin;
    public       postgres    false    194   3�       )	           0    0    grafo_workflow_id    SEQUENCE SET     @   SELECT pg_catalog.setval('public.grafo_workflow_id', 1, false);
            public       postgres    false    208            �          0    16653    grupo 
   TABLE DATA               8   COPY public.grupo (id_grupo, nm_nome_grupo) FROM stdin;
    public       postgres    false    181   P�       *	           0    0    grupo_id    SEQUENCE SET     6   SELECT pg_catalog.setval('public.grupo_id', 2, true);
            public       postgres    false    202            	          0    16814    grupo_usuario 
   TABLE DATA               =   COPY public.grupo_usuario (id_usuario, id_grupo) FROM stdin;
    public       postgres    false    192   y�       �          0    16715    imagem_perfil 
   TABLE DATA               `   COPY public.imagem_perfil (id_imagem_perfil, nm_imagem_perfil_conteudo, id_usuario) FROM stdin;
    public       postgres    false    186   ��       +	           0    0    imagem_perfil_id    SEQUENCE SET     ?   SELECT pg_catalog.setval('public.imagem_perfil_id', 1, false);
            public       postgres    false    209            �          0    16660    projeto_cientifico 
   TABLE DATA               T   COPY public.projeto_cientifico (id_projeto_cientifico, nm_nome_projeto) FROM stdin;
    public       postgres    false    182   ��       ,	           0    0    projeto_cientifico_id    SEQUENCE SET     C   SELECT pg_catalog.setval('public.projeto_cientifico_id', 1, true);
            public       postgres    false    198             	          0    16751    tag 
   TABLE DATA               2   COPY public.tag (id_tag, nm_nome_tag) FROM stdin;
    public       postgres    false    189   �       -	           0    0    tag_id    SEQUENCE SET     4   SELECT pg_catalog.setval('public.tag_id', 2, true);
            public       postgres    false    200            	          0    16863 
   tag_tarefa 
   TABLE DATA               7   COPY public.tag_tarefa (id_tag, id_tarefa) FROM stdin;
    public       postgres    false    195   �       	          0    16781    tarefa 
   TABLE DATA               �   COPY public.tarefa (id_tarefa, nm_descricao, dt_data_limite, nr_tempo_estimado, nm_status, nm_titulo_tarefa, nm_url_repositorio, dt_data_criacao, id_workflow, id_projeto_cientifico, id_fase, id_usuario, id_grupo) FROM stdin;
    public       postgres    false    191   A�       .	           0    0 	   tarefa_id    SEQUENCE SET     7   SELECT pg_catalog.setval('public.tarefa_id', 2, true);
            public       postgres    false    201            �          0    16704    usuario 
   TABLE DATA               {   COPY public.usuario (id_usuario, nm_email, nm_instituicao, bo_tem_imagem_perfil, nm_nome, nm_senha, nm_perfil) FROM stdin;
    public       postgres    false    185   Ε       /	           0    0 
   usuario_id    SEQUENCE SET     8   SELECT pg_catalog.setval('public.usuario_id', 2, true);
            public       postgres    false    203            	          0    16878    usuario_tarefa 
   TABLE DATA               ?   COPY public.usuario_tarefa (id_usuario, id_tarefa) FROM stdin;
    public       postgres    false    196   ��       �          0    16679    workflow 
   TABLE DATA               �   COPY public.workflow (id_workflow, nm_versao, nm_nome_workflow, nm_swfms, id_grupo, id_projeto_cientifico, id_experimento_cientifico) FROM stdin;
    public       postgres    false    184   ��       0	           0    0    workflow_id    SEQUENCE SET     9   SELECT pg_catalog.setval('public.workflow_id', 2, true);
            public       postgres    false    205            	          0    0    BLOBS    BLOBS                                false   �       V           2606    16729    arquivo_modelo_pk 
   CONSTRAINT     m   ALTER TABLE ONLY public.arquivo_modelo
    ADD CONSTRAINT arquivo_modelo_pk PRIMARY KEY (id_arquivo_modelo);
 J   ALTER TABLE ONLY public.arquivo_modelo DROP CONSTRAINT arquivo_modelo_pk;
       public         postgres    false    187    187            e           2606    16833    documentacao_pk 
   CONSTRAINT     g   ALTER TABLE ONLY public.documentacao
    ADD CONSTRAINT documentacao_pk PRIMARY KEY (id_documentacao);
 F   ALTER TABLE ONLY public.documentacao DROP CONSTRAINT documentacao_pk;
       public         postgres    false    193    193            ^           2606    16765    execucao_workflow_pk 
   CONSTRAINT     v   ALTER TABLE ONLY public.execucao_workflow
    ADD CONSTRAINT execucao_workflow_pk PRIMARY KEY (id_execucao_workflow);
 P   ALTER TABLE ONLY public.execucao_workflow DROP CONSTRAINT execucao_workflow_pk;
       public         postgres    false    190    190            H           2606    16673 .   experimento_cientifico_nm_nome_experimento_key 
   CONSTRAINT     �   ALTER TABLE ONLY public.experimento_cientifico
    ADD CONSTRAINT experimento_cientifico_nm_nome_experimento_key UNIQUE (nm_nome_experimento);
 o   ALTER TABLE ONLY public.experimento_cientifico DROP CONSTRAINT experimento_cientifico_nm_nome_experimento_key;
       public         postgres    false    183    183            J           2606    16671    experimento_cientifico_pk 
   CONSTRAINT     �   ALTER TABLE ONLY public.experimento_cientifico
    ADD CONSTRAINT experimento_cientifico_pk PRIMARY KEY (id_experimento_cientifico);
 Z   ALTER TABLE ONLY public.experimento_cientifico DROP CONSTRAINT experimento_cientifico_pk;
       public         postgres    false    183    183            X           2606    16745    fase_pk 
   CONSTRAINT     O   ALTER TABLE ONLY public.fase
    ADD CONSTRAINT fase_pk PRIMARY KEY (id_fase);
 6   ALTER TABLE ONLY public.fase DROP CONSTRAINT fase_pk;
       public         postgres    false    188    188            g           2606    16857    grafo_workflow_pk 
   CONSTRAINT     m   ALTER TABLE ONLY public.grafo_workflow
    ADD CONSTRAINT grafo_workflow_pk PRIMARY KEY (id_grafo_workflow);
 J   ALTER TABLE ONLY public.grafo_workflow DROP CONSTRAINT grafo_workflow_pk;
       public         postgres    false    194    194            @           2606    16659    grupo_nm_nome_grupo_key 
   CONSTRAINT     a   ALTER TABLE ONLY public.grupo
    ADD CONSTRAINT grupo_nm_nome_grupo_key UNIQUE (nm_nome_grupo);
 G   ALTER TABLE ONLY public.grupo DROP CONSTRAINT grupo_nm_nome_grupo_key;
       public         postgres    false    181    181            B           2606    16657    grupo_pk 
   CONSTRAINT     R   ALTER TABLE ONLY public.grupo
    ADD CONSTRAINT grupo_pk PRIMARY KEY (id_grupo);
 8   ALTER TABLE ONLY public.grupo DROP CONSTRAINT grupo_pk;
       public         postgres    false    181    181            c           2606    16818    grupo_usuario_pk 
   CONSTRAINT     n   ALTER TABLE ONLY public.grupo_usuario
    ADD CONSTRAINT grupo_usuario_pk PRIMARY KEY (id_usuario, id_grupo);
 H   ALTER TABLE ONLY public.grupo_usuario DROP CONSTRAINT grupo_usuario_pk;
       public         postgres    false    192    192    192            T           2606    16719    imagem_perfil_pk 
   CONSTRAINT     j   ALTER TABLE ONLY public.imagem_perfil
    ADD CONSTRAINT imagem_perfil_pk PRIMARY KEY (id_imagem_perfil);
 H   ALTER TABLE ONLY public.imagem_perfil DROP CONSTRAINT imagem_perfil_pk;
       public         postgres    false    186    186            D           2606    16666 &   projeto_cientifico_nm_nome_projeto_key 
   CONSTRAINT        ALTER TABLE ONLY public.projeto_cientifico
    ADD CONSTRAINT projeto_cientifico_nm_nome_projeto_key UNIQUE (nm_nome_projeto);
 c   ALTER TABLE ONLY public.projeto_cientifico DROP CONSTRAINT projeto_cientifico_nm_nome_projeto_key;
       public         postgres    false    182    182            F           2606    16664    projeto_cientifico_pk 
   CONSTRAINT     y   ALTER TABLE ONLY public.projeto_cientifico
    ADD CONSTRAINT projeto_cientifico_pk PRIMARY KEY (id_projeto_cientifico);
 R   ALTER TABLE ONLY public.projeto_cientifico DROP CONSTRAINT projeto_cientifico_pk;
       public         postgres    false    182    182            Z           2606    16757    tag_nm_nome_tag_key 
   CONSTRAINT     Y   ALTER TABLE ONLY public.tag
    ADD CONSTRAINT tag_nm_nome_tag_key UNIQUE (nm_nome_tag);
 A   ALTER TABLE ONLY public.tag DROP CONSTRAINT tag_nm_nome_tag_key;
       public         postgres    false    189    189            \           2606    16755    tag_pk 
   CONSTRAINT     L   ALTER TABLE ONLY public.tag
    ADD CONSTRAINT tag_pk PRIMARY KEY (id_tag);
 4   ALTER TABLE ONLY public.tag DROP CONSTRAINT tag_pk;
       public         postgres    false    189    189            i           2606    16867    tag_tarefa_pk 
   CONSTRAINT     e   ALTER TABLE ONLY public.tag_tarefa
    ADD CONSTRAINT tag_tarefa_pk PRIMARY KEY (id_tag, id_tarefa);
 B   ALTER TABLE ONLY public.tag_tarefa DROP CONSTRAINT tag_tarefa_pk;
       public         postgres    false    195    195    195            a           2606    16788 	   tarefa_pk 
   CONSTRAINT     U   ALTER TABLE ONLY public.tarefa
    ADD CONSTRAINT tarefa_pk PRIMARY KEY (id_tarefa);
 :   ALTER TABLE ONLY public.tarefa DROP CONSTRAINT tarefa_pk;
       public         postgres    false    191    191            P           2606    16714    usuario_nm_email_key 
   CONSTRAINT     [   ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_nm_email_key UNIQUE (nm_email);
 F   ALTER TABLE ONLY public.usuario DROP CONSTRAINT usuario_nm_email_key;
       public         postgres    false    185    185            R           2606    16712 
   usuario_pk 
   CONSTRAINT     X   ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_pk PRIMARY KEY (id_usuario);
 <   ALTER TABLE ONLY public.usuario DROP CONSTRAINT usuario_pk;
       public         postgres    false    185    185            k           2606    16882    usuario_tarefa_pk 
   CONSTRAINT     q   ALTER TABLE ONLY public.usuario_tarefa
    ADD CONSTRAINT usuario_tarefa_pk PRIMARY KEY (id_usuario, id_tarefa);
 J   ALTER TABLE ONLY public.usuario_tarefa DROP CONSTRAINT usuario_tarefa_pk;
       public         postgres    false    196    196    196            L           2606    16688    workflow_nm_nome_workflow_key 
   CONSTRAINT     m   ALTER TABLE ONLY public.workflow
    ADD CONSTRAINT workflow_nm_nome_workflow_key UNIQUE (nm_nome_workflow);
 P   ALTER TABLE ONLY public.workflow DROP CONSTRAINT workflow_nm_nome_workflow_key;
       public         postgres    false    184    184            N           2606    16686    workflow_pk 
   CONSTRAINT     [   ALTER TABLE ONLY public.workflow
    ADD CONSTRAINT workflow_pk PRIMARY KEY (id_workflow);
 >   ALTER TABLE ONLY public.workflow DROP CONSTRAINT workflow_pk;
       public         postgres    false    184    184            _           1259    16893 &   idx_tarefa_workflow_projeto_cientifico    INDEX     w   CREATE INDEX idx_tarefa_workflow_projeto_cientifico ON public.tarefa USING btree (id_workflow, id_projeto_cientifico);
 :   DROP INDEX public.idx_tarefa_workflow_projeto_cientifico;
       public         postgres    false    191    191            q           2606    16730    arquivo_modelo_usuario_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.arquivo_modelo
    ADD CONSTRAINT arquivo_modelo_usuario_fk FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario);
 R   ALTER TABLE ONLY public.arquivo_modelo DROP CONSTRAINT arquivo_modelo_usuario_fk;
       public       postgres    false    187    185    2130            r           2606    16735    arquivo_modelo_workflow_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.arquivo_modelo
    ADD CONSTRAINT arquivo_modelo_workflow_fk FOREIGN KEY (id_workflow) REFERENCES public.workflow(id_workflow);
 S   ALTER TABLE ONLY public.arquivo_modelo DROP CONSTRAINT arquivo_modelo_workflow_fk;
       public       postgres    false    187    2126    184            �           2606    16844 &   documentacao_experimento_cientifico_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.documentacao
    ADD CONSTRAINT documentacao_experimento_cientifico_fk FOREIGN KEY (id_experimento_cientifico) REFERENCES public.experimento_cientifico(id_experimento_cientifico);
 ]   ALTER TABLE ONLY public.documentacao DROP CONSTRAINT documentacao_experimento_cientifico_fk;
       public       postgres    false    2122    183    193                       2606    16839 "   documentacao_projeto_cientifico_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.documentacao
    ADD CONSTRAINT documentacao_projeto_cientifico_fk FOREIGN KEY (id_projeto_cientifico) REFERENCES public.projeto_cientifico(id_projeto_cientifico);
 Y   ALTER TABLE ONLY public.documentacao DROP CONSTRAINT documentacao_projeto_cientifico_fk;
       public       postgres    false    182    2118    193            ~           2606    16834    documentacao_workflow_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.documentacao
    ADD CONSTRAINT documentacao_workflow_fk FOREIGN KEY (id_workflow) REFERENCES public.workflow(id_workflow);
 O   ALTER TABLE ONLY public.documentacao DROP CONSTRAINT documentacao_workflow_fk;
       public       postgres    false    2126    193    184            v           2606    16776 #   execucao_workflow_arquivo_modelo_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.execucao_workflow
    ADD CONSTRAINT execucao_workflow_arquivo_modelo_fk FOREIGN KEY (id_arquivo_modelo) REFERENCES public.arquivo_modelo(id_arquivo_modelo);
 _   ALTER TABLE ONLY public.execucao_workflow DROP CONSTRAINT execucao_workflow_arquivo_modelo_fk;
       public       postgres    false    2134    187    190            t           2606    16766    execucao_workflow_usuario_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.execucao_workflow
    ADD CONSTRAINT execucao_workflow_usuario_fk FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario);
 X   ALTER TABLE ONLY public.execucao_workflow DROP CONSTRAINT execucao_workflow_usuario_fk;
       public       postgres    false    2130    185    190            u           2606    16771    execucao_workflow_workflow_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.execucao_workflow
    ADD CONSTRAINT execucao_workflow_workflow_fk FOREIGN KEY (id_workflow) REFERENCES public.workflow(id_workflow);
 Y   ALTER TABLE ONLY public.execucao_workflow DROP CONSTRAINT execucao_workflow_workflow_fk;
       public       postgres    false    2126    190    184            l           2606    16674 ,   experimento_cientifico_projeto_cientifico_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.experimento_cientifico
    ADD CONSTRAINT experimento_cientifico_projeto_cientifico_fk FOREIGN KEY (id_projeto_cientifico) REFERENCES public.projeto_cientifico(id_projeto_cientifico);
 m   ALTER TABLE ONLY public.experimento_cientifico DROP CONSTRAINT experimento_cientifico_projeto_cientifico_fk;
       public       postgres    false    183    182    2118            s           2606    16746    fase_projeto_cientifico_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.fase
    ADD CONSTRAINT fase_projeto_cientifico_fk FOREIGN KEY (id_projeto_cientifico) REFERENCES public.projeto_cientifico(id_projeto_cientifico);
 I   ALTER TABLE ONLY public.fase DROP CONSTRAINT fase_projeto_cientifico_fk;
       public       postgres    false    2118    188    182            �           2606    16858    grafo_workflow_workflow_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.grafo_workflow
    ADD CONSTRAINT grafo_workflow_workflow_fk FOREIGN KEY (id_workflow) REFERENCES public.workflow(id_workflow);
 S   ALTER TABLE ONLY public.grafo_workflow DROP CONSTRAINT grafo_workflow_workflow_fk;
       public       postgres    false    194    184    2126            |           2606    16819    grupo_usuario_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.grupo_usuario
    ADD CONSTRAINT grupo_usuario_fk FOREIGN KEY (id_grupo) REFERENCES public.grupo(id_grupo);
 H   ALTER TABLE ONLY public.grupo_usuario DROP CONSTRAINT grupo_usuario_fk;
       public       postgres    false    192    181    2114            p           2606    16720    imagem_perfil_usuario_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.imagem_perfil
    ADD CONSTRAINT imagem_perfil_usuario_fk FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario);
 P   ALTER TABLE ONLY public.imagem_perfil DROP CONSTRAINT imagem_perfil_usuario_fk;
       public       postgres    false    2130    185    186            �           2606    16868    tag_tarefa_fk    FK CONSTRAINT     x   ALTER TABLE ONLY public.tag_tarefa
    ADD CONSTRAINT tag_tarefa_fk FOREIGN KEY (id_tag) REFERENCES public.tag(id_tag);
 B   ALTER TABLE ONLY public.tag_tarefa DROP CONSTRAINT tag_tarefa_fk;
       public       postgres    false    195    189    2140            {           2606    16809    tarefa_fase_fk    FK CONSTRAINT     x   ALTER TABLE ONLY public.tarefa
    ADD CONSTRAINT tarefa_fase_fk FOREIGN KEY (id_fase) REFERENCES public.fase(id_fase);
 ?   ALTER TABLE ONLY public.tarefa DROP CONSTRAINT tarefa_fase_fk;
       public       postgres    false    2136    188    191            w           2606    16789    tarefa_grupo_fk    FK CONSTRAINT     |   ALTER TABLE ONLY public.tarefa
    ADD CONSTRAINT tarefa_grupo_fk FOREIGN KEY (id_grupo) REFERENCES public.grupo(id_grupo);
 @   ALTER TABLE ONLY public.tarefa DROP CONSTRAINT tarefa_grupo_fk;
       public       postgres    false    2114    181    191            y           2606    16799    tarefa_projeto_cientifico_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.tarefa
    ADD CONSTRAINT tarefa_projeto_cientifico_fk FOREIGN KEY (id_projeto_cientifico) REFERENCES public.projeto_cientifico(id_projeto_cientifico);
 M   ALTER TABLE ONLY public.tarefa DROP CONSTRAINT tarefa_projeto_cientifico_fk;
       public       postgres    false    2118    191    182            �           2606    16873    tarefa_tag_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.tag_tarefa
    ADD CONSTRAINT tarefa_tag_fk FOREIGN KEY (id_tarefa) REFERENCES public.tarefa(id_tarefa);
 B   ALTER TABLE ONLY public.tag_tarefa DROP CONSTRAINT tarefa_tag_fk;
       public       postgres    false    191    195    2145            z           2606    16804    tarefa_usuario_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.tarefa
    ADD CONSTRAINT tarefa_usuario_fk FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario);
 B   ALTER TABLE ONLY public.tarefa DROP CONSTRAINT tarefa_usuario_fk;
       public       postgres    false    191    2130    185            �           2606    16888    tarefa_usuario_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.usuario_tarefa
    ADD CONSTRAINT tarefa_usuario_fk FOREIGN KEY (id_tarefa) REFERENCES public.tarefa(id_tarefa);
 J   ALTER TABLE ONLY public.usuario_tarefa DROP CONSTRAINT tarefa_usuario_fk;
       public       postgres    false    196    191    2145            x           2606    16794    tarefa_workflow_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.tarefa
    ADD CONSTRAINT tarefa_workflow_fk FOREIGN KEY (id_workflow) REFERENCES public.workflow(id_workflow);
 C   ALTER TABLE ONLY public.tarefa DROP CONSTRAINT tarefa_workflow_fk;
       public       postgres    false    2126    184    191            }           2606    16824    usuario_grupo_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.grupo_usuario
    ADD CONSTRAINT usuario_grupo_fk FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario);
 H   ALTER TABLE ONLY public.grupo_usuario DROP CONSTRAINT usuario_grupo_fk;
       public       postgres    false    185    192    2130            �           2606    16883    usuario_tarefa_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.usuario_tarefa
    ADD CONSTRAINT usuario_tarefa_fk FOREIGN KEY (id_usuario) REFERENCES public.usuario(id_usuario);
 J   ALTER TABLE ONLY public.usuario_tarefa DROP CONSTRAINT usuario_tarefa_fk;
       public       postgres    false    185    2130    196            n           2606    16694 "   workflow_experimento_cientifico_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.workflow
    ADD CONSTRAINT workflow_experimento_cientifico_fk FOREIGN KEY (id_experimento_cientifico) REFERENCES public.experimento_cientifico(id_experimento_cientifico);
 U   ALTER TABLE ONLY public.workflow DROP CONSTRAINT workflow_experimento_cientifico_fk;
       public       postgres    false    2122    184    183            o           2606    16699    workflow_grupo_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.workflow
    ADD CONSTRAINT workflow_grupo_fk FOREIGN KEY (id_grupo) REFERENCES public.grupo(id_grupo);
 D   ALTER TABLE ONLY public.workflow DROP CONSTRAINT workflow_grupo_fk;
       public       postgres    false    2114    184    181            m           2606    16689    workflow_projeto_cientifico_fk    FK CONSTRAINT     �   ALTER TABLE ONLY public.workflow
    ADD CONSTRAINT workflow_projeto_cientifico_fk FOREIGN KEY (id_projeto_cientifico) REFERENCES public.projeto_cientifico(id_projeto_cientifico);
 Q   ALTER TABLE ONLY public.workflow DROP CONSTRAINT workflow_projeto_cientifico_fk;
       public       postgres    false    184    182    2118            �   r   x�u�A
�0����@���jb��&��E^�i����@��,�*"�}���w��ӱl�\���k��V���"����Ay��M��SZ�M��:/���Tr���jmtƘ6d*m      	      x������ � �      	   V   x�3�N�t.�-�)-�4�3�,�/�N��/�M�IK,��M�
����s���420��54�54P02�20�25�37�4Bc�=... ��_      �   #   x�3�t�(H-��M�+�WI-.I�4����� }�      �   7   x�3�t��KN-8����|�4NC.CNǼ�s2�S�\#N׊��R�|	P F��� �      	      x������ � �      �      x�3�I-.I5�2�0�b���� D�n      	      x�3�4�2�4�2�=... (      �      x������ � �      �      x�3�(��J-�WI-.I����� I�       	   "   x�3�,I-.I�2�L�HM.=����|�=... xM�      	      x�3�4�2�4����� ��      	   }   x�3��"O?w΀�ļԬ�"�����Ԃ��/�WH�/V(I-"�B#Cs]C]#SCC+c+#=3SCN4�\FP#ML���V�&�� M-�/�N��/�j�����	�N# ������� �%):      �   �   x�5���0 E��;�+��b��A�
�K�A����z���ޜ3�\Ս�r9�^]��D��x�@3�f�Zv�0�J9L��l�rg�u��i�/yȸ�.;�$B�}���G� �X!hEs.���Z6Y6�.ᐲG��X��!-����.�6z���Ó����3֓>�V�މ�d9�����l�^���C�      	      x�3�4����� d      �   <   x�3�4�3��/�N��/7�N�t.�-�)-�4�4�4�2DQ��Z\�����*F��� E�      	   I   �  x��V[o�0~f���e���+��Ȥ��*%��^��뜶ޜ8�Z���F�u-<�'��s���^1�+�B���O�.d3�d�Bf�����A���@�Km�Th��D3�i�(��g��<�<�`�>z���u.�Ii�>ÈI������2X�O��0��Ai2�F*HV��4C.����2���uy����U�߄zO5���6'�X���\=�TkK�4g�������j+�a!OR�eE䦠�r�6�4S<7\f��v�K��6���=��㪮\��dz�0*����S/9(��u����!�5�ʉw���	�MӒ�t��*%�Q�W�|�%[�qaV�q�F5^�0ۙ�J�Y�H4��hxaG�*�x�h���kWb�ؓ8.����N���k��:Ux;	�C0�㋱��f�i�zUte�j'DR���I�����a����d�|�v��T�37�_6���A��R�C*m[��{��n+g�9�e�/W����&҉�Ga��5x�����1H�D�[���cu]T T��n��_�Kj9�"��-��~Y�A�(r��ؼ��eg)��dg��������e|�%�\�%�F�S&�%����n#�0�]�z/X�[vY*\I�n����������-/��f���Z$��[�|#�ǅ��.��5HC���W�Xs���!�($>�      �I   �  x��V[o�0~f���e���+��Ȥ��*%��^��뜶ޜ8�Z���F�u-<�'��s���^1�+�B���O�.d3�d�Bf�����A���@�Km�Th��D3�i�(��g��<�<�`�>z���u.�Ii�>ÈI������2X�O��0��Ai2�F*HV��4C.����2���uy����U�߄zO5���6'�X���\=�TkK�4g�������j+�a!OR�eE䦠�r�6�4S<7\f��v�K��6���=��㪮\��dz�0*����S/9(��u����!�5�ʉw���	�MӒ�t��*%�Q�W�|�%[�qaV�q�F5^�0ۙ�J�Y�H4��hxaG�*�x�h���kWb�ؓ8.����N���k��:Ux;	�C0�㋱��f�i�zUte�j'DR���I�����a����d�|�v��T�37�_6���A��R�C*m[��{��n+g�9�e�/W����&҉�Ga��5x�����1H�D�[���cu]T T��n��_�Kj9�"��-��~Y�A�(r��ؼ��eg)��dg��������e|�%�\�%�F�S&�%����n#�0�]�z/X�[vY*\I�n����������-/��f���Z$��[�|#�ǅ��.��5HC���W�Xs���!�($>�      �I   �  x��VMs�0=ә��fr$nhz�;�`��`��|�EV���d�}W�6.�@>�d�߾�}Z��^Q�/�B���/�es�d��� �ș��q�b4g�^j8?�vJ4��6�p ���σ�g�QC=\[�:ܤ$�`D�b�ç���P��O���K3��97R9���MH;�Kmmg�w�{]:�\�y�C7!���PFR�IS�è���sN���9Ke<���;<񰐔�'��J�rSqS�F���(a�*�.3c��q	5�^,��t���n�j<���K�Q�C�����3ŭ\�4M ��\.G�� �&GNm���e����(9<,CDY^\)��lǅi���j8{�&�p3�k�𓺐h����Z�(�x���յ��(���b��%���~�N�N����Ɠ�|�_bĳ9\�J��������H�҃p4�C�c9��Z$�⛲FyJhLm���:��a��-q��k����㻭��O���J�U,j�.z��?;��� ���Ah�����7���#��IK�ASf��2�
�ʐ�Q��:�S��-��eB~|6��0�,r�%,��jr�eZ�V�r��iK�{��i��/��p߁����'d<}m��M�ڔٛ$xɆ�u����r炍����\,2�	�F��?���\��C�M4����#{�KC�q8���f���̮�ߚ�U�@+      mK   �  x��VMs�0=ә��fr$nhz�;�`��`��|�EV���d�}W�6.�@>�d�߾�}Z��^Q�/�B���/�es�d��� �ș��q�b4g�^j8?�vJ4��6�p ���σ�g�QC=\[�:ܤ$�`D�b�ç���P��O���K3��97R9���MH;�Kmmg�w�{]:�\�y�C7!���PFR�IS�è���sN���9Ke<���;<񰐔�'��J�rSqS�F���(a�*�.3c��q	5�^,��t���n�j<���K�Q�C�����3ŭ\�4M ��\.G�� �&GNm���e����(9<,CDY^\)��lǅi���j8{�&�p3�k�𓺐h����Z�(�x���յ��(���b��%���~�N�N����Ɠ�|�_bĳ9\�J��������H�҃p4�C�c9��Z$�⛲FyJhLm���:��a��-q��k����㻭��O���J�U,j�.z��?;��� ���Ah�����7���#��IK�ASf��2�
�ʐ�Q��:�S��-��eB~|6��0�,r�%,��jr�eZ�V�r��iK�{��i��/��p߁����'d<}m��M�ڔٛ$xɆ�u����r炍����\,2�	�F��?���\��C�M4����#{�KC�q8���f���̮�ߚ�U�@+      nK   �  x��VMo�0=����R�4[�{#+�*T
���8!�Z�:qd;����$P
�~�ə�y3�<L�y�E+�3���߿՚�̄VI�%v��O{�VУd�^|�2i�z9���X�����7��i��-{�ii�39eV�@	W�OOw�0)�����ǀ6�LX��P�̥c���8��[f�r�_�%��Nu�Jac�q#f�=3@cE��%�\}N�1HU�J[��:��g��>��3���]��!��7Eh�R�*��Z�V�ħ�yך�[�E�N�t��+��j��'����܇-^{�N�cG�8�̢ˑ�	��#�4Mr�<p��A���$�,-��m2���hR��[����u$N�B���}��ck0m���غ�v3��݄��2�:Y$+�� שڷ����&���|\R"�^�R��.��2 ������!��uXm�
Qy�U�p3�s��?6�v��v��+q��k-�n�w[9_�y����XR)Z\�&�qpvڳ�Ah[��І�Oк���z���J�W��2�R���!��"��՚^9�L�Ɩ	����x�l̳H!� ��br�gZ6��r��Y����i���\��(��]|A�����$oM��I��l�ݦ�z9Xl\�/eNҝ+�C�*���?�gK�ja4�D�x��p��qa�?�n2P.0ޫl���^,���}?�          