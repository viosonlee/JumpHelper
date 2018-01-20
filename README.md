# JumpHelper
微信跳一跳游戏辅助
<h3>思路</h3>
1.获取需要跳跃的距离；
  通过WindowManager发送一个悬浮窗到“跳一跳游戏”的上面，然后手指点击起点和终点，计算出距离<br>
2.按压时间是距离和<a href="https://github.com/stackisok/wechat_jumponejump_cheat/blob/master/src/JumpMain.java">倍数</a>的乘积<br>
3.模拟按压；参考<a href="http://blog.csdn.net/mad1989/article/details/38109689">Android通过代码模拟物理、屏幕点击事件</a>
