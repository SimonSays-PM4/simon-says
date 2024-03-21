import { Table } from "@radix-ui/themes";
import { Button } from "../../components/Button";
import { useNavigate } from "react-router-dom";

export const EventListPage: React.FC = () => {
    const navigate = useNavigate();

    return (
        <div>
            <h1 className="font-bold flex items-center justify-start text-[24px] sm:text-[26px] xl:text-[32px] mb-8">
                Events
            </h1>

            <Button buttonText="Create" />

            <Table.Root className="pt-4">
                <Table.Header>
                    <Table.Row>
                        <Table.ColumnHeaderCell>Name</Table.ColumnHeaderCell>
                        <Table.ColumnHeaderCell>Date</Table.ColumnHeaderCell>
                        <Table.ColumnHeaderCell></Table.ColumnHeaderCell>
                    </Table.Row>
                </Table.Header>

                <Table.Body>
                    <Table.Row>
                        <Table.RowHeaderCell>Test 123</Table.RowHeaderCell>
                        <Table.Cell>01.06.2024</Table.Cell>
                        <Table.Cell className="flex min-h-[60px]">
                            <Button buttonText="Edit" onClick={() => navigate("/event/id123")} />
                            <Button buttonText="Delete" className="ml-2" />
                        </Table.Cell>
                    </Table.Row>

                    <Table.Row>
                        <Table.RowHeaderCell>qwer asdf</Table.RowHeaderCell>
                        <Table.Cell>12.12.2024</Table.Cell>
                        <Table.Cell className="flex min-h-[60px]">
                            <Button buttonText="Edit" onClick={() => navigate("/event/id123")} />
                            <Button buttonText="Delete" className="ml-2" />
                        </Table.Cell>
                    </Table.Row>
                </Table.Body>
            </Table.Root>
        </div>
    );
}