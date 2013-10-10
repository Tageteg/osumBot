package com.osum.bot;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class WorldList extends JPanel
{
	public enum HostCountry {
	    USA,
	    UK,
	    GERMANY,
	    UNKNOWN;
	 
	    public static HostCountry getHostCountry(int countryId) {
	        switch (countryId) {
	            case 0:
	                return HostCountry.USA;
	            case 1:
	                return HostCountry.UK;
	            case 7:
	                return HostCountry.GERMANY;
	            default:
	                return HostCountry.UNKNOWN;
	        }
	    }
	}
	
	public class ByteStream {
	    public ByteStream(byte abyte0[]) {
	        data = abyte0;
	        caret = 0;
	    }
	 
	    public int g1() {
	        return data[caret++] & 0xff;
	    }
	 
	    public byte g1b() {
	        return data[caret++];
	    }
	 
	    public int g2() {
	        caret += 2;
	        return ((data[caret - 2] & 0xff) << 8) + (data[caret - 1] & 0xff);
	    }
	 
	    public int g2b() {
	        caret += 2;
	        int i = ((data[caret - 2] & 0xff) << 8) + (data[caret - 1] & 0xff);
	        if (i > 32767) {
	            i -= 0x10000;
	        }
	        return i;
	    }
	 
	    public int g3() {
	        caret += 3;
	        return ((data[caret - 3] & 0xff) << 16) + ((data[caret - 2] & 0xff) << 8) + (data[caret - 1] & 0xff);
	    }
	 
	    public int g4() {
	        caret += 4;
	        return ((data[caret - 4] & 0xff) << 24) + ((data[caret - 3] & 0xff) << 16) + ((data[caret - 2] & 0xff) << 8) + (data[caret - 1] & 0xff);
	    }
	 
	    public String gstr() {
	        int i = caret;
	        while (data[caret++] != 0) ;
	        return new String(data, i, caret - i - 1);
	    }
	    public byte data[];
	    public int caret;
	}
	
	public class World {
	    private final int worldId;
	    private final boolean members;
	    private final HostCountry hostCountry;
	    private final String host;
	    private int players;
	 
	    World(int worldId, String host, boolean members, HostCountry hostCountry, int numberOfPlayers) {
	        this.worldId = worldId;
	        this.host = host;
	        this.members = members;
	        this.hostCountry = hostCountry;
	        this.players = numberOfPlayers;
	    }
	 
	    public String getHost() {
	        return host;
	    }
	 
	    public HostCountry getHostCountry() {
	        return hostCountry;
	    }
	 
	    public boolean isMembers() {
	        return members;
	    }
	 
	    public int getPlayers() {
	        return players;
	    }
	 
	    public int getWorldId() {
	        return worldId;
	    }
	 
	    public void setPlayers(int players) {
	        this.players = players;
	    }
	 
	    @Override
	    public String toString() {
	        return "World{" + "worldId=" + worldId + ", members=" + members + ", hostCountry=" + hostCountry + ", host=" + host + ", players=" + players + '}';
	    }
	}
	
    class IntComparator implements Comparator<Integer> {
        public int compare(Integer o1, Integer o2) {
            return o1.compareTo(o2);
        }

        public boolean equals(Object o2) {
            return this.equals(o2);
        }
    }
	
	private static final long serialVersionUID = 1L;
	
	public static final WorldList CURRENT = new WorldList();

	private final String[] columnNames = { "World", "Members", "Country", "Players"};
	private final HashMap<Integer, World> worlds = new HashMap<Integer, World>();
    private final DefaultTableModel model;
    private int globalPlayerCount;
	private final JTable table;
    private String address;
    private int count;
    
	private WorldList()
    {
    	setBackground(Color.BLACK);
    	this.table = new JTable();
    	table.setModel(this.model = new DefaultTableModel(new Object[0][0], columnNames)
    	{
			private static final long serialVersionUID = 1L;
    		
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex)
			{
				switch (columnIndex)
				{
					case 0:
					case 3:
						return Integer.class;
				}
				
				return Object.class;
			}
    	});
    	DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
    	renderer.setAlignmentX(DefaultTableCellRenderer.LEFT);
    	table.getColumn(columnNames[0]).setCellRenderer(renderer);
        /*TableRowSorter trs = new TableRowSorter(model);
        trs.setComparator(0, new IntComparator());
        trs.setComparator(3, new IntComparator());
        table.setRowSorter(trs);*/
    	table.setAutoCreateRowSorter(true);
    	JScrollPane scrollPane = new JScrollPane(table);
    	table.setFillsViewportHeight(true);
    	add(scrollPane);
    }
    
    public void setAddress(String address)
    {
    	this.address = address;
    }
 
    public void update() throws IOException {
        globalPlayerCount = 0;
        HttpURLConnection url = (HttpURLConnection) new URL(address).openConnection();
        DataInputStream in = new DataInputStream(url.getInputStream());
        byte data[] = new byte[url.getContentLength()];
        in.readFully(data);
        ByteStream p = new ByteStream(data);
        p.caret = 4; //skip first 4, might be a session id or timestamp
        int count = p.g2(); //   aq = dl1.ag(0x804d807d) * 0x54db551e;
        Object[][] dv = new Object[count][columnNames.length];
        
        for (int i = 0; i < count; i++) {
            int j1 = p.g2();  // int j1 = dl1.ag(0x83d47af5);
            int worldId = (j1 & 0x7fff); // n1.c = 0xbe183a66 * (j1 & 0x7fff);
            boolean isMembers = j1 != 0;// n1.e = 0 != (j1 & 0xc5af3c81);
            String host = p.gstr(); // n1.f = dl1.ai(0x3bd62f26);
            int countryId = p.g1();// n1.y = dl1.az(0xdb8be48a) * 0x5932670f;
            int numberOfPlayers = p.g2b(); // n1.j = dl1.am((byte)-30) * 0xa5d501b;
            // n1.x = i1 * 0x53f5e1ef;
            dv[i][0] = worldId - 300;
            dv[i][1] = isMembers;
            dv[i][2] = HostCountry.getHostCountry(countryId);
            dv[i][3] = numberOfPlayers;
            World w = new World(worldId, host, isMembers, HostCountry.getHostCountry(countryId), numberOfPlayers);
            this.worlds.put(worldId, w);
            this.globalPlayerCount += numberOfPlayers;
        }
        
        model.setDataVector(dv, columnNames);
        model.fireTableDataChanged();
        
        //BotWindow.newTitle("total players: " + globalPlayerCount);
        
        invalidate();
        repaint();
    }
 
    public int getNumberOfPlayers() {
        return globalPlayerCount;
    }
 
    public int getWorldCount() {
        return count;
    }
 
    public String getWorldListAddress() {
        return address;
    }
 
    public HashMap<Integer, World> getWorlds() {
        return worlds;
    }
}
