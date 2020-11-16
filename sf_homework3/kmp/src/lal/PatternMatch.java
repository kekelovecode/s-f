package lal;

public class PatternMatch {
    public static void main(String[] args) {
        String [] strs1=new String[5000];    //主串数组
        String [] strs2=new String[5000];    //模式串数组
        int [] kmpstatus=new int[5000];      //kmp下标数组
        int [] montecarlostatus=new int[5000];//montecarlo下标数组
        for(int i=0;i<5000;i++)   //初始化主串和模式串
        {   int random=100+(int)(Math.random()*100);
            strs1[i]=zeroOneRandom(random);
            strs2[i]=zeroOneRandom(random/10);
        }
        int []prime=new int[5000];          //素数数组
        for(int i=0;i<strs2.length;i++)//初始化素数
        {
            int min=strs2[i].length();
            prime[i]=random_prime(min*min*min*10,Integer.MAX_VALUE);
        }
        long time1= System.currentTimeMillis();
        for(int i=0;i<strs1.length;i++){
            kmpstatus[i]=kmpPatternMatch(strs1[i],strs2[i]);  //将kmp返回的坐标存储
        }
        long time2= System.currentTimeMillis();
        System.out.println("KMP算法时间为 "+(time2-time1));
        /*************************************************************/

        time1= System.currentTimeMillis();
        for(int i=0;i<strs1.length;i++)
           montecarlostatus[i]= MonteCarlo(strs1[i],strs2[i],prime[i]);
        time2= System.currentTimeMillis();
        System.out.println("Monte Carlo算法时间为 "+(time2-time1));
        double flag=0;
        for(int i=0;i<5000;i++) {
            if (kmpstatus[i] != montecarlostatus[i])
                flag++;                                  //计算montecarlo错误率
        }
        double flag1=flag/strs1.length;
        System.out.println("5000次模式匹配，MonteCarlo算法错误率为"+flag1*100+"%");
        time1= System.currentTimeMillis();
        for(int i=0;i<strs1.length;i++)
        LasVegas(strs1[i],strs2[i],prime[i]);
        time2= System.currentTimeMillis();
        System.out.println("LasVegas算法时间为 "+(time2-time1));
    }
    public static String zeroOneRandom(int length)          //随机生成指定长度的0，1串
    {   String a=new String();
        char []chs={'0','1'};
        for(int i=0;i<length;i++)
        {
            int m=(int)(Math.random()*2);
            a+=chs[m];
        }
        return a;
    }

    public static int[] getNext(char[] p) {
        int pLen = p.length;
        int[] next = new int[pLen];
        int k = -1; //k为前缀的下标
        int j = 0;  //j为后缀的下标
        next[0] = -1; // next数组中next[0]为-1
        while (j < pLen - 1) {
            if (k == -1 || p[j] == p[k]) {     //当前字符匹配，若k=-1代表不断回溯，最长公共串长度为0
                k++;
                j++;
                next[j] = k;    //当满足k=-1这个条件，next[j]肯定=0，表示字符不匹配
            } else {
                k = next[k];
                //字符不匹配时，next[k]代表到k时，前k个字符的最长公共串，
                // 也就是说当前k不满足条件的话,让k=next[k]意思时让k回溯到在k点的最长前缀对应下标值，如果对应说明之前的也匹配，只要下次循环k加一即可
            }
        }
        return next;
    }
public static int kmpPatternMatch(String source, String pattern) {
    int i = 0, j = 0;
    char[] src = source.toCharArray();
    char[] ptn = pattern.toCharArray();
    int sLen = src.length;
    int pLen = ptn.length;
    int[] next = getNext(ptn);
    while (i < sLen && j < pLen) {
        // 如果j = -1,或者当前字符匹配成功(src[i] = ptn[j]),都让i++,j++
        if (j == -1 || src[i] == ptn[j]) {
            i++;
            j++;
        } else {
            // 如果j!=-1且当前字符匹配失败,则令i不变,j=next[j],即让pattern模式串右移j-next[j]个单位
            j = next[j];
        }
    }
    if (j == pLen)
        return i - j;
    return -1;
}
    public static boolean isprime(int x){
        for(int i=2;i<x;i++)
            if(x%i==0&&i<x)	return false;	//注意2是质数
        return true;
    }
    //随机产生一个[min, max-1]区间上的素数
    public static int random_prime( int min, int max )
    {
        int su=0;
        su= (int) (Math.random()%(max-min)+min);
        for(; su >= min; su--)
            if(isprime(su)==true) break;
        return su;
    }
    //字符串和模式串是由0-1组成，将字符串和模式串由二进制转换为十进制,对字符串的十进制编码与P做取模运算
    static int getIP(char []x,int len,int p)
    {
        int ip = 0;
        for(int k = 0 ; k < len; k++)
            ip = ( 2 * ip  + x[k] - '0') % p;
        return ip;
    }
    //函数名：MonteCarlo
    //功能：利用随机算法MonteCarlo进行模式匹配
    //输入：主串s和模式串t,,随机素数p
    //输出：模式串在主串第pos个字符之后出现的位置
    //             x为主串   y为模式串, pos初始值为1,p为随机生成的素数
    static int MonteCarlo(String x, String y, int p)
    {
        char []cx=x.toCharArray();
        char []cy=y.toCharArray();
        int j = 0;
        int Ipx, Ipy, wp;
        int x_len = cx.length;
        int y_len = cy.length;
        //取指纹
        String sub=x.substring(0, cy.length);
        Ipx=getIP(cx,y_len,p);
        Ipy=getIP(cy,y_len,p);
        //计算2m mod pc
        wp = 1;
        for(int i = 0; i < y_len; i++)
            wp = (wp * 2) % p;
        //开始匹配模式串
//        j=79
        while( j < x_len - y_len)
        {

            if(Ipx == Ipy)
                return j+1;
            else
            {
                //ipx代表的字符串右移一位的指纹值
                Ipx = ( 2 * Ipx - wp * ( cx[j] - '0' ) + (cx[j + y_len] - '0') ) % p;
                if(Ipx < 0) Ipx += p;
                if(Ipx >= p) Ipx -= p;
                j++;
            }
        }
        return -1;
    }
    public   static int LasVegas(String x, String y, int p)
    {   char []cx=x.toCharArray();
        char []cy=y.toCharArray();
        int j = 0;
        int Ipx, Ipy, wp;
        int x_len = cx.length;
        int y_len = cy.length;
        //取指纹
        Ipx = getIP(cx,y_len,p);
        Ipy = getIP(cy,y_len,p);
        //计算2m mod p
        wp = 1;
        for(int i = 0; i < y_len; i++)
            wp = (wp * 2) % p;
        //开始匹配模式串
        while( j < x_len -y_len)
        {
            boolean chayi= false;
            String a=String.valueOf(cx,j,cy.length);
            chayi=a.equals(y);
            //如果指纹相同，还需要判断当前串是否匹配
            if(Ipx == Ipy && chayi==true)
                return j ;
            else
            {    //ipx代表的字符串右移一位的指纹值
                Ipx = ( 2 * Ipx - wp * ( cx[j] - '0' ) + (cx[j + y_len] - '0') ) % p;
                if(Ipx < 0)
                    Ipx += p;
                if(Ipx >= p)
                    Ipx -= p;
                j++;
            }
        }
        return -1;
    }

}
