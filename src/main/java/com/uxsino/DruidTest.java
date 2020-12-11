package com.uxsino;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitorAdapter;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.stat.TableStat;
import org.junit.Test;

import java.util.List;

public class DruidTest {
    public static void main(String[] args) {
//        String sql = "insert into sb (id,name) values (1,'test');";
//        String sql = "select age a,name n from students s where id ='1';";
        String sql = "select age a,name n from student s inner join (select id,name from score where sex='女')" +
                " temp on sex='男' and temp.id in(select id from score where sex='男') where student.name='zhangsan' " +
                "group by student.age order by student.id ASC;";
        //创建parser
//        MySqlStatementParser parser = new MySqlStatementParser(sql);
        PGSQLStatementParser parser = new PGSQLStatementParser(sql);
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        PGASTVisitor s = new PGASTVisitorAdapter();
        //parser对象.parseStatement();获取SQLStatement语法树
        SQLStatement sqlStatement = parser.parseStatement();

        //SQLStatement对象.accept(visitor对象);
        sqlStatement.accept(visitor);

        //通过visitor访问语法树的每个部分

        //getTables()返回sql中包含的表名
        System.out.println("table="+visitor.getTables());

        //getDbType返回mysql
        System.out.println("dbType="+visitor.getDbType());

        //返回表名.列名
        System.out.println("getColumns="+visitor.getColumns());

        //返回条件
        List<TableStat.Condition> conditions = visitor.getConditions();
        System.out.println("-----------------------------------------------");
        conditions.stream().forEach(e-> System.out.println("Condition="+e));
        System.out.println("-----------------------------------------------");


        System.out.println("getColumn_students.id="+visitor.getColumn("students","id")); //这个方法莫名其妙的
        System.out.println("getColumn_student.lala="+visitor.getColumn("student","lala")); //这个方法莫名其妙的
        System.out.println("getColumn_student.name="+visitor.getColumn("student","name")); //这个方法莫名其妙的
        System.out.println("getColumn_score.age="+visitor.getColumn("score","age")); //这个方法莫名其妙的
        //如果传入的table存在且表中有传入的字段名,则返回    表名.字段名  否则返回 null

        //获取order by 的列名
        System.out.println("orderBy : " + visitor.getOrderByColumns());


    }
    @Test
    public void test(){
//        SQLStatementParser ssp = new SQLStatementParser();
    }
}
