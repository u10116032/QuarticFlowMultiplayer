public abstract class ResponseHandler {
    protected Manager manager;

    public ResponseHandler(Manager manager)
    {
        this.manager = manager;
    }

    public abstract void execute(byte[] contents);
}
