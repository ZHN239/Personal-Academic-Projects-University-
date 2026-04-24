using UnityEngine;

public class character : MonoBehaviour
{
    // Start is called once before the first execution of Update after the MonoBehaviour is created
    public bool is_ground;
    public Animator anima;
    void Start()
    {
    }

    // Update is called once per frame
    void Update()
    {
        Debug.Log(GetComponent<Rigidbody2D>().linearVelocity);
        if (transform.position.x < -3.6f|| transform.position.y<-2.4f)
        {
            anima.SetBool("die", true);
        }
        if (Input.GetKeyDown(KeyCode.Space))
        {
            jump();
        }
    }
    public void jump()
    {
        if (is_ground == true)
        {
            AudioManager.instance.play("Ìø");
            gameObject.GetComponent<Rigidbody2D>().AddForce(Vector2.up * 200);
        }
    }


    private void OnCollisionEnter2D(Collision2D collision)
    {
        anima.SetBool("jump", false);
        if (collision.gameObject.tag == "Ground")
        {
            is_ground = true;
        }
    }
    private void OnCollisionExit2D(Collision2D collision)
    {
        anima.SetBool("jump", true);
        is_ground = false;
    }
}
