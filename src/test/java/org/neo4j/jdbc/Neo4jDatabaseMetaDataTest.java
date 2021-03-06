/**
 * Copyright (c) 2002-2011 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.neo4j.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * TODO
 */
public class Neo4jDatabaseMetaDataTest extends Neo4jJdbcTest
{

    public Neo4jDatabaseMetaDataTest( Mode mode ) throws SQLException
    {
        super( mode );
    }

    @Test
    public void testGetTables() throws SQLException
    {
        createTableMetaData( gdb );
        ResultSet rs = conn.getMetaData().getTables( null, null, "%", null );

        System.out.println( rs );
    }

    private void createTableMetaData( GraphDatabaseService gdb )
    {
        try ( Transaction tx = gdb.beginTx() )
        {
            final Node tables = gdb.createNode();
            final Node table = gdb.createNode();
            final Node column = gdb.createNode();
            tx.success();
        }
    }

    @Test
    public void testGetColumns() throws SQLException
    {
        ResultSet rs = conn.getMetaData().getColumns( null, null, "%", null );

        System.out.println( rs );
    }
}
