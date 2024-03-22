export const Topbar: React.FC = () => {
    return (
        <div className="w-full pt-[16px] flex items-center border-b border-default-200 h-[80px]">
            <div className="hidden lg:block w-[3.33%]"></div>
            <div className="md:bg-white md:h-full md:w-[45%] lg:w-[33.33%] flex items-center justify-end md:top-[-150px] md:left-10 top-[25px] right-[25px] sm:left-[65%] absolute md:static">
                <p className="my-auto">Header</p>
            </div>
        </div>
    );
};
