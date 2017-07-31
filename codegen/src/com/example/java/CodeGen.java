package com.example.java;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassMember;
import polyglot.ast.FieldDecl;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Import;
import polyglot.ast.MethodDecl;
import polyglot.ast.PackageNode;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.TypeNode;
import polyglot.ext.jl5.ast.JL5ExtFactory_c;
import polyglot.ext.jl7.JL7ExtensionInfo;
import polyglot.ext.jl7.ast.J7Lang_c;
import polyglot.ext.jl7.ast.JL7ExtFactory_c;
import polyglot.ext.jl7.ast.JL7NodeFactory;
import polyglot.ext.jl7.ast.JL7NodeFactory_c;
import polyglot.ext.jl7.types.JL7TypeSystem;
import polyglot.ext.jl7.types.JL7TypeSystem_c;
import polyglot.main.Options;
import polyglot.types.Flags;
import polyglot.types.Package_c;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.util.SimpleCodeWriter;
import polyglot.visit.PrettyPrinter;

public class CodeGen {
	public static void main(String[] args) throws URISyntaxException {
		// To avoid NullReferenceException when Options.global is referred by PrettyPrinter
		Options.global = new Options(new JL7ExtensionInfo());

		// Java 7 Node factory
		JL7NodeFactory nf = new JL7NodeFactory_c(J7Lang_c.instance,
				new JL7ExtFactory_c(new JL5ExtFactory_c()));

		JL7TypeSystem tysys = new JL7TypeSystem_c();

		// An example of Java 7 AST
		Position pos = Position.COMPILER_GENERATED;


		// Class Declaration
		Id clzName = nf.Id(pos, "Test");
		TypeNode parentClz = null;
		

		// Field Decl
		Id fieldName = nf.Id(pos, "env");
		Id fieldtypeName = nf.Id(pos, "Env");
		TypeNode fieldtype = nf.AmbTypeNode(pos, fieldtypeName);
		FieldDecl fieldDecl = nf.FieldDecl(pos, Flags.STATIC, fieldtype, fieldName);
		

		// Method Decl
		Id methodName = nf.Id(pos, "eval");
		Id methodtypeName = nf.Id(pos, "void");
		//TypeNode methodtype = nf.AmbTypeNode(pos, methodtypeName);
		TypeNode methodtype = new TypeNode.Instance(pos, null);
		methodtype.type();
		Id formalName = nf.Id(pos, "args");
		Id formaltypeName = nf.Id(pos, "String[]");
		TypeNode formaltype = nf.AmbTypeNode(pos, formaltypeName);
		Formal formal = nf.Formal(pos, Flags.NONE, null, formaltype, formalName);
		List<Formal> formals = new ArrayList<>();
		formals.add(formal);
		List<Stmt> stmts = new ArrayList<>();
		stmts.add(nf.Return(pos));
		Block block = nf.Block(pos, stmts);
		MethodDecl methodDecl = nf.MethodDecl(pos, Flags.PUBLIC, null, methodtype, methodName, formals, null, block, null, null);
		
		List<ClassMember> members = new ArrayList<>();
		members.add(fieldDecl);
		members.add(methodDecl);
		
		ClassBody clzBody = nf.ClassBody(pos, members);
		ClassDecl clzDecl =
				nf.ClassDecl(pos, Flags.PUBLIC, null, clzName, parentClz,
						null, clzBody, null, null);

		ArrayList<TopLevelDecl> toplevelDecls = new ArrayList<TopLevelDecl>();
		toplevelDecls.add(clzDecl);

		// Package name
		PackageNode pkg = nf.PackageNode(pos, new Package_c(tysys, "com.example.java"));

		// Package Import Declaration
		Import imp = nf.Import(pos, Import.SINGLE_TYPE, "java.util.ArrayList");

		ArrayList<Import> imports = new ArrayList<Import>();
		imports.add(imp);

		// Source file
		SourceFile srcFile = nf.SourceFile(pos, pkg, imports, toplevelDecls);

		// Print it out!!
		PrettyPrinter pp = new PrettyPrinter(J7Lang_c.instance);
		CodeWriter cw = new SimpleCodeWriter(System.out, 4);
		pp.printAst(srcFile, cw);
		// TODO: <<<< null >>> is printed.
		// The source of SourceFile is set to be null strangely.
	}
}
