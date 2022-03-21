import lombok.AllArgsConstructor;
import lombok.Data;

public class SynchronTest implements Runnable{

    static ListNode listNode = ListNode.getByNum(new int[]{2, 1, 3, 5});
    public static void main(String[] args) {
        get();
        new Thread(new SynchronTest()).start();
        get();
        get();
        new Thread(new SynchronTest()).start();
        get();
        new Thread(new SynchronTest()).start();
        get();
        new Thread(new SynchronTest()).start();
        get();
        new Thread(new SynchronTest()).start();
        get();
        get();
    }

    @Override
    public void run() {
        ListNode f = listNode;
        synchronized (f) {
            if (f != null) {
                f.show();
            }
        }
        System.out.println("run结束");
    }
    
    public static void get() {
        ListNode f = listNode;
        synchronized (f) {
            if (f != null) {
                f.show();
            }
        }
        System.out.println("get结束");
    }



}

@Data
@AllArgsConstructor
class ListNode {
    int val;
    ListNode next;

    public static ListNode getByNum(int[] nums) {
        ListNode hair = new ListNode(0, null), temp = hair;
        for (int i = 0; i < nums.length; i++) {
            temp.next = new ListNode(nums[i], null);
            temp = temp.next;
        }
        return hair.next;
    }

    public void show() {
        System.out.println(Thread.currentThread().getName() + "正在读取" + this.val);
    }
}
