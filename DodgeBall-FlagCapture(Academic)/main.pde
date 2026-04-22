float[][]ball_position;             //各小球の位置情報を保存する　　n個目の小球のｘ座標はball_position[n-1][0],y座標はball_position[n-1][1]     ただし、n<=number_difficulty
float[][]ball_velocity;             //各小球の速度情報を保存する    n個目の小球のｘ方向の速度はball_velocity[n-1][0],ｙ方向の速度はball_velocity[n-1][1]     ただし、n<=number_difficulty
int square_size=1000;
int number_difficulty=20;       //動いている小球の数を決める。Ｃ言語の場合、scanf関数でユーザーに決めさせることが可能
int width=200,depth=200;
float diameter=30;      //動いている小球の直径を3０とする
float collision_volume=50;//マウスを中心とする円の直径を決める
int[][]monitor;    //初期値は０で、マウスと旗の距離が一度半径より小さくなった場合、それに対応するmonitorを1とする
float x_adjust=10; //旗の判定範囲(点)を調整する
float y_adjust=20; //旗の判定範囲(点)を調整する
void drawflag(float x,float y)   //旗の最も下の点の座標を(x,y)とする,色が変わる前の旗を描く
{
  line(x,y,x,y-40);
  fill(196);
  triangle(x,y-20,x+20,y-30,x,y-40);
}
void color_change(float x,float y)//旗の最も下の点の座標を(x,y)とする,色が変わった後の旗を描く
{
  line(x,y,x,y-40);
  fill(0,255,0);
  triangle(x,y-20,x+20,y-30,x,y-40);
}
void drawball(float x,float y,float diameter)     //小球を描く
{
  fill(255,0,0);
  ellipse(x,y,diameter,diameter); 
}
int judge(float xy)                    //小球の動く方向を決める
{
  int flag=1;
  if(xy>=square_size-diameter/2||xy<=diameter/2)
  {
    flag=-1;
  }
  return flag;
}
float distance(float x1,float y1,float x2,float y2)             //両点の間の距離を計算する
{
  float x,y;
  float result;
  x=x1-x2;
  y=y1-y2;
    x*=x;
    y*=y;
  result=sqrt(x+y);
  return result;
}
void setup()
{
  int i1,i2;
  float speed_difficulty=4;
    size(1000,1000);
  background(255);   
  monitor=new int[square_size/depth][square_size/width];
  ball_position=new float[number_difficulty][2];
  ball_velocity=new float[number_difficulty][2];
  for(i1=0;i1<square_size/width;i1++)
  {
    for(i2=0;i2<square_size/depth;i2++)
    {
    drawflag(i1*width+100,i2*depth+100);
    monitor[i1][i2]=0;              //全部の旗のmonitorを0とする
    }
  }
  for(i1=0;i1<number_difficulty;i1++)                               //difficulty個の小球の位置と速度を初期化する
  {
 for(i2=0;i2<2;i2++)
 {
   ball_position[i1][i2]=random(diameter/2,square_size-diameter/2);           
   ball_velocity[i1][i2]=random(-1*speed_difficulty,speed_difficulty);
 }
  }
  for(i1=0;i1<number_difficulty;i1++)                              //difficulty個の小球の最初位置を表す
  {
   drawball(ball_position[i1][0],ball_position[i1][1],diameter);
 } 
  }  
void draw()
{
  int i1,i2;
  background(255);
   for(i1=0;i1<number_difficulty;i1++)                              //マウスから動いている小球の中心までの距離がマウスを中心とする円の半径＋小球の半径より小さい場合（つまり、マウスを中心とする円が小球と接触している）、背景の色を黄色に変える
  {
       if(distance(ball_position[i1][0],ball_position[i1][1],mouseX,mouseY)<=(diameter+collision_volume)/2)
   {
     background(255,242,0);
   }
  }
    for(i1=0;i1<square_size/width;i1++)
  {
    for(i2=0;i2<square_size/depth;i2++)
    {
      if(distance(i1*width+100+x_adjust,i2*depth+100-y_adjust,mouseX,mouseY)<=collision_volume/2) //旗の判定範囲（点）がマウスを中心とする円の内部に入っていたら、その旗のmonitorを１とする
      {
        monitor[i1][i2]=1;
      }
      if(monitor[i1][i2]==1)//monitorが一度１になったら、０に戻れないため、旗の色が常に緑色になっている
      {
        color_change(i1*width+100,i2*depth+100);
      }
      else
      {
      drawflag(i1*width+100,i2*depth+100);
      }
    }
  }
    fill(0);
    ellipse(mouseX,mouseY,collision_volume,collision_volume);//マウスを中心とする円を示す
   for(i1=0;i1<number_difficulty;i1++)                              
  {
 for(i2=0;i2<2;i2++)
 {
   ball_velocity[i1][i2]*=judge(ball_position[i1][i2]);//judge関数でflagが戻ってくるから、flag=1の場合、小球がそのまま動くのだが、flag=-1の場合、逆方向に動き始める
 }
  }
   for(i1=0;i1<number_difficulty;i1++)                              
  {
 for(i2=0;i2<2;i2++)
 {
   ball_position[i1][i2]+=ball_velocity[i1][i2];//小球今の位置は前の位置より速度*Δt程度ずれている
 }
  }
   for(i1=0;i1<number_difficulty;i1++)                              
  {
   drawball(ball_position[i1][0],ball_position[i1][1],diameter);//この瞬間、小球のある場所を示す
  }
}
