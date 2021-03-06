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

import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JDBC Driver implementation that is backed by a REST Neo4j Server.
 */
public class Driver implements java.sql.Driver
{

    private final static Log log = LogFactory.getLog( Driver.class );
    public static final String CON_PREFIX = "jdbc:neo4j:";

    static
    {
        try
        {
            DriverManager.registerDriver( new Driver() );
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }
    }

    static final String LEGACY = "legacy";
    static final String URL_PREFIX = "jdbc:neo4j";
    static final String PASSWORD = "password";
    static final String USER = "user";

    DriverQueries queries;

    public Driver()
    {
        queries = new DriverQueries();
    }

    public Neo4jConnection connect( String url, Properties properties ) throws SQLException
    {
        if ( !acceptsURL( url ) )
        {
            return null;
        }
        parseUrlProperties( url, properties );

        return Connections.create( this, url, properties );
    }

    public boolean acceptsURL( String s ) throws SQLException
    {
        return s.startsWith( CON_PREFIX );
    }

    public DriverPropertyInfo[] getPropertyInfo( String s, Properties props ) throws SQLException
    {
        return new DriverPropertyInfo[]
                {
                        infoFor( props, "debug" ),
                        infoFor( props, "user" ),
                        infoFor( props, "password" )
                };
    }

    private DriverPropertyInfo infoFor( Properties properties, String name )
    {
        return new DriverPropertyInfo( name, properties.getProperty( name ) );
    }

    public int getMajorVersion()
    {
        return 1;
    }

    public int getMinorVersion()
    {
        return 0;
    }

    public boolean jdbcCompliant()
    {
        return false;
    }

    public DriverQueries getQueries()
    {
        return queries;
    }

    void parseUrlProperties( String s, Properties properties )
    {
        if ( s.contains( "?" ) )
        {
            String urlProps = s.substring( s.indexOf( '?' ) + 1 );
            String[] props = urlProps.split( "," );
            for ( String prop : props )
            {
                int idx = prop.indexOf( '=' );
                if ( idx != -1 )
                {
                    String key = prop.substring( 0, idx );
                    String value = prop.substring( idx + 1 );
                    properties.put( key, value );
                }
                else
                {
                    properties.put( prop, "true" );
                }
            }
        }
    }

    private final Databases databases = createDatabases();

    private Databases createDatabases()
    {
        try
        {
            return (Databases) Class.forName( "org.neo4j.jdbc.embedded.EmbeddedDatabases" ).newInstance();
        }
        catch ( Throwable e )
        {
            log.warn( "Embedded Neo4j support not enabled " + e.getMessage() );
            return null;
        }
    }

    public QueryExecutor createExecutor( String connectionUrl, Properties properties ) throws SQLException
    {
        if ( databases == null )
        {
            throw new SQLFeatureNotSupportedException( "Embedded Neo4j not available please add neo4j-kernel, " +
                    "-index and -cypher to the classpath" );
        }
        return databases.createExecutor( connectionUrl, properties );
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException
    {
        return null;
    }
}
