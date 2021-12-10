package Generate;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;



public class Generate {
    int[][] mapImage;
    int[][] map;
    int width;
    int height;
    int pixel;
    ArrayList<ArrayList<Integer[][]>> xyisland = new ArrayList<>();


    public Generate(int w, int h, int k, int p){
        this.mapImage = new int[w][h];
        this.width=w;
        this.height = h;
        this.pixel=p;
        this.map = new int [w/p][h/p];
        MyVectorNoise(k);
        Pixelation(p);
        this.mapImage=MapBuilding.setID(mapImage,w,h,(byte)1);
    }

    void MyVectorNoise (int k){
        MyVectorNoise noise = new MyVectorNoise(this.width,this.height,k);
        this.mapImage = noise.getNoise();
    }

    void Pixelation (int k){
        this.mapImage = MapBuilding.pixilate(this.width,this.height,k,this.mapImage);
    }

    public int[][] getIdMap(){
        for(int x=0;x<this.width;x+=pixel){
            for (int y=0;y<this.height;y+=pixel){
                map[x/pixel][y/pixel] = mapImage[x][y];
            }
        }
        return map;
    }


    public BufferedImage getMapImage() {
        //int max = 0;
        BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {

                /*if (this.mapImage[x][y] > max) {
                    max = this.mapImage[x][y];
                }*/

                int finalValue = 65536 *this.mapImage[x][y]+256*this.mapImage[x][y]+this.mapImage[x][y];
                /*int alpha = (r >> 24) & 0xff;

                int red = (r >> 16) & 0xff;

                int green = (r >> 8) & 0xff;

                int blue = (r) & 0xff;

                System.out.println("ARGB : " + alpha + ", " + red + ", " + green + ", " + blue);
                */
                image.setRGB(x, y, finalValue);
            }
        }
        //System.out.println(max);
        return image;
    }


    public BufferedImage getMapIDImage() {
        //int max = 0;
        BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {

                /*if (this.mapImage[x][y] > max) {
                    max = this.mapImage[x][y];
                }*/
                //System.out.printf("Цвет ID %d%n", mapImage[x][y]);

                int finalValue = switch (this.mapImage[x][y]) {
                    case (0) -> 65536 * 139 + 256 * 69 + 19;
                    case (1) -> 65536 * 240 + 256 * 230 + 140;
                    case (2) -> 65536 * 152 + 256 * 251 + 152;
                    case (3) -> 65536 * 105 + 256 * 105 + 105;
                    case (4) -> 0;
                    default -> 0;
                };

                //int finalValue = 65536 *this.mapImage[x][y]+256*this.mapImage[x][y]+this.mapImage[x][y];
                /*int alpha = (r >> 24) & 0xff;

                int red = (r >> 16) & 0xff;

                int green = (r >> 8) & 0xff;

                int blue = (r) & 0xff;

                System.out.println("ARGB : " + alpha + ", " + red + ", " + green + ", " + blue);
                */
                image.setRGB(x, y, finalValue);
            }
        }
        //System.out.println(max);
        return image;
    }

    private static int[][] copyArray(int[][] origin)
    {
        int[][] copy = new int[origin.length][origin[0].length];
        for ( int i = 0 ; i < origin.length; ++i)
        {
            System.arraycopy(origin[i], 0, copy[i], 0, origin[i].length);
        }
        return copy;
    }

    public int numIslands() {
        int[][] grid = copyArray(map);
        int m = grid.length;
        int n = grid[0].length;
        short island = 0;
        for (int i =0; i<m;i++){
            for (int j = 0; j < n; j++){
                if(checkWalk(grid[i][j])){
                    this.xyisland.add(new ArrayList<>());
                    island++;
                    System.out.println("Island  "+island);
                    checkIsland(grid, m, n, i, j,island);
                }
            }
        }
        return island;
    }

    boolean checkWalk(int c){
        boolean g;
        switch (c) {
            case (3), (4) -> g = false;
            case (0), (1), (2) -> g = true;
            default -> throw new IllegalStateException("Unexpected value: " + c);
        }
        return g;
    }

    void checkIsland(int[][] grid, int m, int n, int i, int j, short island){

        if ((i<0)||(j<0)||(i>=m)||(j>=n)||(!checkWalk(grid[i][j]))){
            return;
        }
        Integer[][] k = new Integer[1][1];
        k[0][0]=grid[i][j];
        (this.xyisland.get(island-1)).add(k);
        k=null;
        grid[i][j]=4;
        System.out.println(island+"  "+(this.xyisland.get(island-1)).size());
        checkIsland(grid, m, n, i+1, j,island);
        checkIsland(grid, m, n, i, j+1,island);
        checkIsland(grid, m, n, i-1, j,island);
        checkIsland(grid, m, n, i, j-1,island);
    }

    int getIslandArea(short i){
        return this.xyisland.get(i).size();
    }

    int getCountIsland(){
        return numIslands();
    }

    public static void main(String[] args) throws IOException {
        int count = 1;
        boolean c = true;
        int k,p;
        Scanner in = new Scanner(System.in);
        do {
            System.out.printf("Width: %d   Height: %d\n", 12*50,12*50);
            System.out.print("");
            System.out.print("Input a K (k+1): ");
            k = in.nextInt();
            System.out.print("Input a P: ");
            p = in.nextInt();

            Generate M = new Generate(12 * 50, 12 * 50, k, p);// k+1 кратно w и h, p кратно w и h
            //MyVectorNoise noise = new MyVectorNoise(12*50,12*50,19);
            //PerlinNoise noise = new PerlinNoise(11*50,11*50);
            int [][] map = M.getIdMap();
            for(int y =0; y< map[0].length;y++){
                for (int x=0; x<map.length; x++){
                    System.out.print(map[x][y]+" ");
                }
                System.out.printf("\n");
            }
            BufferedImage image = M.getMapIDImage();
            String str = "C://Users//P//Desktop//Test_" + count;
            str = str + "_";
            str = str + k;
            str = str + "_";
            str = str + p;
            str = str + ".jpg";
            File output_pix = new File(str);
            ImageIO.write(image, "jpg", output_pix);
            count++;
            int ci= M.getCountIsland();
            System.out.println(M.getCountIsland());

            for (short i=0;i<ci;i++) {
                System.out.println(M.getIslandArea(i));
            }

        }while(c);
        in.close();
    }
}
