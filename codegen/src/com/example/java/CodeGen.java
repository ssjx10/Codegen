package com.example.java;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import polyglot.ast.Assign;
import polyglot.ast.Binary;
import polyglot.ast.Block;
import polyglot.ast.Call;
import polyglot.ast.Catch;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassLit;
import polyglot.ast.ClassMember;
import polyglot.ast.Expr;
import polyglot.ast.FieldDecl;
import polyglot.ast.ForInit;
import polyglot.ast.ForUpdate;
import polyglot.ast.Formal;
import polyglot.ast.Id;
import polyglot.ast.Import;
import polyglot.ast.IntLit;
import polyglot.ast.MethodDecl;
import polyglot.ast.PackageNode;
import polyglot.ast.SourceFile;
import polyglot.ast.Stmt;
import polyglot.ast.TopLevelDecl;
import polyglot.ast.TypeNode;
import polyglot.ast.Unary;
import polyglot.ext.jl5.ast.AnnotationElemDecl_c;
import polyglot.ext.jl5.ast.JL5ExtFactory_c;
import polyglot.ext.jl7.JL7ExtensionInfo;
import polyglot.ext.jl7.ast.J7Lang_c;
import polyglot.ext.jl7.ast.JL7ExtFactory_c;
import polyglot.ext.jl7.ast.JL7NodeFactory;
import polyglot.ext.jl7.ast.JL7NodeFactory_c;
import polyglot.ext.jl7.types.JL7TypeSystem;
import polyglot.ext.jl7.types.JL7TypeSystem_c;
import polyglot.main.Options;
import polyglot.types.ArrayType_c;
import polyglot.types.ClassType_c;
import polyglot.types.Flags;
import polyglot.types.Package_c;
import polyglot.types.PrimitiveType;
import polyglot.types.PrimitiveType_c;
import polyglot.types.ReferenceType_c;
import polyglot.types.Type;
import polyglot.types.Type_c;
import polyglot.types.UnknownType_c;
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
		Id fieldName1 = nf.Id(pos, "debug");
		TypeNode fieldtype1 = nf.CanonicalTypeNode(pos, new PrimitiveType_c(tysys, PrimitiveType.BOOLEAN));
		FieldDecl fieldDecl1 = nf.FieldDecl(pos, Flags.STATIC, fieldtype1, fieldName1);
		Id fieldName2 = nf.Id(pos, "programArgs");
		Id fieldtypeName2 = nf.Id(pos, "String");
		TypeNode fieldtype2 = nf.ArrayTypeNode(pos, nf.AmbTypeNode(pos, fieldtypeName2));
		FieldDecl fieldDecl2 = nf.FieldDecl(pos, Flags.STATIC.Private(), fieldtype2, fieldName2);


		// Method Decl
		Id methodName = nf.Id(pos, "eval");
		Id methodlabel = nf.Id(pos, "label");
		Id methodtypeName = nf.Id(pos, "void");
		//TypeNode methodtype = nf.AmbTypeNode(pos, methodtypeName);
		TypeNode methodtype = nf.CanonicalTypeNode(pos, new PrimitiveType_c(tysys, PrimitiveType.VOID));
		//TypeNode methodtype = new TypeNode.Instance(pos, null);
		Id formalName = nf.Id(pos, "args");
		Id formaltypeName = nf.Id(pos, "String");
		//TypeNode formaltype = nf.AmbTypeNode(pos, formaltypeName);
		TypeNode formaltype = nf.ArrayTypeNode(pos, nf.AmbTypeNode(pos, formaltypeName));
		Formal formal = nf.Formal(pos, Flags.NONE, null, formaltype, formalName);
		List<Formal> formals = new ArrayList<>();
		formals.add(formal);
		List<Stmt> stmts = new ArrayList<>();
		stmts.add(nf.Eval(pos, nf.FieldAssign(pos, nf.Field(pos, nf.This(pos), fieldName2), Assign.ASSIGN, nf.AmbExpr(pos, formalName))));
		stmts.add(nf.Eval(pos, nf.Call(pos, methodName)));
		stmts.add(nf.Return(pos, nf.AmbExpr(pos, "env")));
		Block block = nf.Block(pos, stmts);
		MethodDecl methodDecl = nf.MethodDecl(pos, Flags.PUBLIC, null, methodtype, methodName, formals, null, block, null, null);

		// Method Decl1
		Id methodName1 = nf.Id(pos, "eval");
		TypeNode methodtype1 = nf.CanonicalTypeNode(pos, new PrimitiveType_c(tysys, PrimitiveType.VOID));
		//TypeNode methodtype5 = new TypeNode.Instance(pos, null);
		List<Stmt> stmts1 = new ArrayList<>();
		stmts1.add(nf.Eval(pos, nf.FieldAssign(pos, nf.Field(pos, nf.This(pos), fieldName), Assign.ASSIGN, nf.New(pos, fieldtype, null))));
		Call callEnvLabel = nf.Call(pos, nf.AmbExpr(pos, fieldName), methodlabel, nf.StringLit(pos, "$main"));
		stmts1.add(nf.Eval(pos, callEnvLabel));

		Stmt w = nf.While(pos, null, nf.Block(pos, 
				nf.LocalDecl(pos, Flags.NONE, nf.AmbTypeNode(pos, nf.Id(pos, "Stmt")), nf.Id(pos, "stmt"),
						nf.Call(pos, nf.AmbExpr(pos, nf.Id(pos, "bbEnv")), nf.Id(pos, "get"), nf.Call(pos, nf.AmbExpr(pos, fieldName), methodlabel))), 
				nf.Eval(pos, nf.Call(pos, nf.AmbExpr(pos, fieldName), methodlabel, nf.NullLit(pos))),
				nf.Eval(pos, nf.Call(pos, methodName, nf.AmbExpr(pos, nf.Id(pos, "bbEnv")), nf.AmbExpr(pos, fieldName), nf.AmbExpr(pos, nf.Id(pos, "Stmt"))))));
		stmts1.add(w);

		Block block1 = nf.Block(pos, stmts1);
		MethodDecl methodDecl1 = nf.MethodDecl(pos, Flags.PUBLIC, null, methodtype1, methodName1, null, null, block1, null, null);
		
		// Method Decl2
		Id methodName2 = nf.Id(pos, "eval");
		TypeNode methodtype2 = nf.CanonicalTypeNode(pos, new PrimitiveType_c(tysys, PrimitiveType.VOID));
		Id formalName2_1 = nf.Id(pos, "bbEnv");
		Id formaltypeName2_1 = nf.Id(pos, "BasicBlockEnv");
		TypeNode formaltype2_1 = nf.AmbTypeNode(pos, formaltypeName2_1);
		Formal formal2_1 = nf.Formal(pos, Flags.NONE, null, formaltype2_1, formalName2_1);
		Formal formal2_2 = nf.Formal(pos, Flags.NONE, null, nf.AmbTypeNode(pos, nf.Id(pos, "Env")), nf.Id(pos, "env"));
		Formal formal2_3 = nf.Formal(pos, Flags.NONE, null, nf.AmbTypeNode(pos, nf.Id(pos, "Assign")), nf.Id(pos, "assignStmt"));
		List<Formal> formals2 = new ArrayList<>();
		formals2.add(formal2_1);
		formals2.add(formal2_2);
		formals2.add(formal2_3);
		List<Stmt> stmts2 = new ArrayList<>();
		stmts2.add(nf.LocalDecl(pos, Flags.NONE, nf.AmbTypeNode(pos, nf.Id(pos, "Expr")), nf.Id(pos, "lhs"),
				nf.Call(pos, nf.AmbExpr(pos, nf.Id(pos, "assignStmt")), nf.Id(pos, "getLSide"))));
		stmts2.add(nf.LocalDecl(pos, Flags.NONE, nf.AmbTypeNode(pos, nf.Id(pos, "Expr")), nf.Id(pos, "rhs"),
				nf.Call(pos, nf.AmbExpr(pos, nf.Id(pos, "assignStmt")), nf.Id(pos, "getRSide"))));
		stmts2.add(nf.LocalDecl(pos, Flags.NONE, nf.AmbTypeNode(pos, nf.Id(pos, "Value")), nf.Id(pos, "v2"),
				nf.Call(pos, nf.Id(pos, "eval"), nf.AmbExpr(pos, nf.Id(pos, "env")), nf.AmbExpr(pos, nf.Id(pos, "rhs")))));

		Stmt i2_1 = nf.If(pos, nf.Instanceof(pos, nf.AmbExpr(pos, nf.Id(pos, "v2")), nf.AmbTypeNode(pos, nf.Id(pos, "ArrayV"))),
				nf.Eval(pos, nf.LocalAssign(pos, nf.Local(pos, nf.Id(pos, "v2")), Assign.ASSIGN, nf.Call(pos, nf.Cast(pos, nf.AmbTypeNode(pos, nf.Id(pos, "ArrayV")), nf.AmbExpr(pos, nf.Id(pos, "v2"))), nf.Id(pos, "copy")))));
		
		List<Stmt> tempStmts = new ArrayList<>();
		tempStmts.add(nf.Eval(pos, nf.Assign(pos, nf.AmbExpr(pos, nf.Id(pos, "elem")), Assign.USHR_ASSIGN, nf.New(pos, nf.AmbTypeNode(pos, nf.Id(pos, "ArrayV")), null))));
		tempStmts.add(nf.Eval(pos, nf.Call(pos, nf.AmbExpr(pos, nf.Id(pos, "env")), nf.Id(pos, "get"), nf.Call(pos, nf.AmbExpr(pos, nf.Id(pos, "arr")), nf.Id(pos, "getVar")), nf.AmbExpr(pos, nf.Id(pos, "elem")))));
		List<ForInit> forInit = new ArrayList<>();
		forInit.add(nf.LocalDecl(pos, Flags.NONE, nf.CanonicalTypeNode(pos, new PrimitiveType_c(tysys, PrimitiveType.INT)), nf.Id(pos, "i"),
				nf.IntLit(pos, IntLit.INT, 0)));
		List<ForUpdate> forUpdate = new ArrayList<>();
		forUpdate.add(nf.Eval(pos, nf.Unary(pos, nf.AmbExpr(pos, nf.Id(pos, "i")), Unary.POST_INC)));
		List<Expr> exprs = new ArrayList<>();
		exprs.add(nf.Binary(pos, nf.StringLit(pos, "Assign : UnKnown lhs "), Binary.ADD, nf.AmbExpr(pos, nf.Id(pos, "lhs"))));
		Stmt i2_2_2 = nf.If(pos, nf.Instanceof(pos, nf.AmbExpr(pos, nf.Id(pos, "lhs")), nf.AmbTypeNode(pos, nf.Id(pos, "Array"))), nf.Block(pos,
				nf.LocalDecl(pos, Flags.NONE, nf.AmbTypeNode(pos, nf.Id(pos, "Array")), nf.Id(pos, "arr"),
						nf.Cast(pos, nf.AmbTypeNode(pos, nf.Id(pos, "Array")), nf.AmbExpr(pos, nf.Id(pos, "lhs")))),
				nf.LocalDecl(pos, Flags.NONE, nf.AmbTypeNode(pos, nf.Id(pos, "Value")), nf.Id(pos, "arrValue"),
						nf.Call(pos, nf.AmbExpr(pos, nf.Id(pos, "env")), nf.Id(pos, "get"), nf.Call(pos, nf.AmbExpr(pos, nf.Id(pos, "arr")), nf.Id(pos, "getVar")))),
				nf.LocalDecl(pos, Flags.NONE, nf.AmbTypeNode(pos, nf.Id(pos, "ArrayV")), nf.Id(pos, "elem")),
				nf.If(pos, nf.Binary(pos, nf.AmbExpr(pos, nf.Id(pos, "arrValue")), Binary.EQ, nf.NullLit(pos)), nf.Eval(pos, nf.Assign(pos, nf.AmbExpr(pos, nf.Id(pos, "elem")), Assign.ASSIGN, nf.NullLit(pos)))),
				nf.If(pos, nf.Binary(pos, nf.AmbExpr(pos, nf.Id(pos, "elem")), Binary.EQ, nf.NullLit(pos)), nf.Block(pos, tempStmts)),
				nf.For(pos, forInit, nf.Binary(pos, nf.AmbExpr(pos, nf.Id(pos, "i")), Binary.LT, nf.Call(pos, nf.AmbExpr(pos, nf.Id(pos, "arr")), nf.Id(pos, "getDim"))), forUpdate, nf.Block(pos,
						nf.LocalDecl(pos, Flags.NONE, nf.AmbTypeNode(pos, nf.Id(pos, "Expr")), nf.Id(pos, "idx"),
								nf.Call(pos, nf.AmbExpr(pos, nf.Id(pos, "arr")), nf.Id(pos, "getIndex"), nf.AmbExpr(pos, nf.Id(pos, "i")))),
						nf.LocalDecl(pos, Flags.NONE, nf.AmbTypeNode(pos, nf.Id(pos, "Value")), nf.Id(pos, "v"),
								nf.Call(pos, nf.Id(pos, "eval"), nf.AmbExpr(pos, nf.Id(pos, "env")), nf.AmbExpr(pos, nf.Id(pos, "idx")))),
						nf.LocalDecl(pos, Flags.NONE, nf.AmbTypeNode(pos, nf.Id(pos, "String")), nf.Id(pos, "idx_s")),
						nf.If(pos, nf.Binary(pos, nf.AmbExpr(pos, nf.Id(pos, "i")), Binary.LT, nf.Binary(pos, nf.Call(pos, nf.AmbExpr(pos, nf.Id(pos, "arr")), nf.Id(pos, "getDim")), Binary.SUB, nf.IntLit(pos, IntLit.INT, 1))), nf.Block(pos,
								nf.LocalDecl(pos, Flags.NONE, nf.AmbTypeNode(pos, nf.Id(pos, "ArrayV")), nf.Id(pos, "elem_elem"),
										nf.Call(pos, nf.AmbExpr(pos, nf.Id(pos, "arr")), nf.Id(pos, "get"), nf.AmbExpr(pos, nf.Id(pos, "idx_s")))),
								nf.If(pos, nf.Binary(pos, nf.AmbExpr(pos, nf.Id(pos, "elem_elem")), Binary.EQ, nf.NullLit(pos)), nf.Block(pos,
										nf.Eval(pos, nf.Assign(pos, nf.AmbExpr(pos, nf.Id(pos, "elem_elem")), Assign.ASSIGN, nf.New(pos, nf.AmbTypeNode(pos, nf.Id(pos, "ArrayV")), null))),
										nf.Eval(pos, nf.Call(pos, nf.AmbExpr(pos, nf.Id(pos, "elem")), nf.Id(pos, "put"), nf.AmbExpr(pos, nf.Id(pos, "idx_s")), nf.AmbExpr(pos, nf.Id(pos, "elem_elem")))))),
								nf.Eval(pos, nf.Assign(pos, nf.AmbExpr(pos, nf.Id(pos, "elem")), Assign.ASSIGN, nf.AmbExpr(pos, nf.Id(pos, "elem"))))),
								nf.Eval(pos, nf.Call(pos, nf.AmbExpr(pos, nf.Id(pos, "elem")), nf.Id(pos, "put"), nf.AmbExpr(pos, nf.Id(pos, "idx_s")), nf.AmbExpr(pos, nf.Id(pos, "v2")))))
						))), nf.Throw(pos, nf.New(pos, nf.AmbTypeNode(pos, nf.Id(pos, "InterpretException")), exprs)));
		exprs.clear();
		exprs.add(nf.Binary(pos, nf.StringLit(pos, "Assign : "), Binary.ADD, nf.Call(pos, nf.AmbExpr(pos, nf.Id(pos, "e")), nf.Id(pos, "toString"))));
		List<TypeNode> tn = new ArrayList<>();
		tn.add(nf.AmbTypeNode(pos, nf.Id(pos, "SecurityException")));
		tn.add(nf.AmbTypeNode(pos, nf.Id(pos, "NoSuchFieldException")));
		Catch c = nf.MultiCatch(pos, nf.Formal(pos, Flags.NONE, null, nf.AmbTypeNode(pos, nf.Id(pos, "NoSuchFieldException")), nf.Id(pos, "e")), tn,
				nf.Block(pos, nf.Throw(pos, nf.New(pos, nf.AmbTypeNode(pos, nf.Id(pos, "InterpretException")), exprs))));
		List<Catch> catchBlocks = new ArrayList<>();
		catchBlocks.add(c);
		Stmt try_ = nf.Try(pos, nf.Block(pos, nf.LocalDecl(pos, Flags.NONE, nf.AmbTypeNode(pos, nf.Id(pos, "String")), nf.Id(pos, "clzName"),
				nf.Call(pos, nf.Cast(pos, nf.AmbTypeNode(pos, nf.Id(pos, "PropertyExpr")), nf.AmbExpr(pos, nf.Id(pos, "lhs"))) ,nf.Id(pos, "getObj"))),
				nf.LocalDecl(pos, Flags.NONE, nf.AmbTypeNode(pos, nf.Id(pos, "Class")), nf.Id(pos, "clz"),
						nf.Call(pos, nf.Id(pos, "getClass"), nf.AmbExpr(pos, nf.Id(pos, "clzName")))),
				nf.LocalDecl(pos, Flags.NONE, nf.AmbTypeNode(pos, nf.Id(pos, "Field")), nf.Id(pos, "fld"),
						nf.Call(pos, nf.AmbExpr(pos, nf.Id(pos, "clz")), nf.Id(pos, "getField"), nf.Call(pos, nf.Cast(pos, nf.AmbTypeNode(pos, nf.Id(pos, "PropertyExpr")), nf.AmbExpr(pos, nf.Id(pos, "lhs"))) ,nf.Id(pos, "getName")))),
				nf.Eval(pos, nf.Call(pos, nf.AmbExpr(pos, nf.Id(pos, "fld")), nf.Id(pos, "set"), nf.NullLit(pos), nf.AmbExpr(pos, nf.Id(pos, "v2")))),
				nf.LocalDecl(pos, Flags.NONE, nf.AmbTypeNode(pos, nf.Id(pos, "Method")), nf.Id(pos, "mth"),
						nf.Call(pos, nf.AmbExpr(pos, nf.Id(pos, "clz")), nf.Id(pos, "getClass"), nf.AmbExpr(pos, nf.Id(pos, "notifyFieldAssign")), nf.ClassLit(pos, nf.AmbTypeNode(pos, nf.Id(pos, "String"))))),
				nf.Eval(pos, nf.Call(pos, nf.AmbExpr(pos, nf.Id(pos, "mth")), nf.Id(pos, "invoke"), nf.NullLit(pos), nf.Call(pos, nf.Cast(pos, nf.AmbTypeNode(pos, nf.Id(pos, "PropertyExpr")), nf.AmbExpr(pos, nf.Id(pos, "lhs"))) ,nf.Id(pos, "getName"))))), catchBlocks);
		Stmt i2_2_1 = nf.If(pos, nf.Instanceof(pos, nf.AmbExpr(pos, nf.Id(pos, "lhs")), nf.AmbTypeNode(pos, nf.Id(pos, "PropertyExpr"))), try_, i2_2_2);
		Stmt i2_2 = nf.If(pos, nf.Instanceof(pos, nf.AmbExpr(pos, nf.Id(pos, "lhs")), nf.AmbTypeNode(pos, nf.Id(pos, "Var"))),
				nf.Eval(pos, nf.Call(pos, nf.AmbExpr(pos, nf.Id(pos, "env")), nf.Id(pos, "put"), nf.Call(pos, nf.Cast(pos, nf.AmbTypeNode(pos, nf.Id(pos, "Var")), nf.AmbExpr(pos, nf.Id(pos, "lhs"))), nf.Id(pos, "getVarName")), nf.AmbExpr(pos, nf.Id(pos, "v2")))),
				i2_2_1);
		stmts2.add(i2_1);
		stmts2.add(i2_2);

		Block block2 = nf.Block(pos, stmts2);
		MethodDecl methodDecl2 = nf.MethodDecl(pos, Flags.PUBLIC, null, methodtype2, methodName2, formals2, null, block2, null, null);


		List<ClassMember> members = new ArrayList<>();
		members.add(fieldDecl);
		members.add(fieldDecl1);
		members.add(fieldDecl2);
		members.add(methodDecl);
		members.add(methodDecl1);
		members.add(methodDecl2);

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
